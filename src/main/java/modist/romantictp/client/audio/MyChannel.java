package modist.romantictp.client.audio;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.SoundBuffer;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.InstrumentSoundInstance;
import modist.romantictp.client.sound.InstrumentSoundManager;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.openal.AL10;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

public class MyChannel extends Channel {  //TODO: Thread safety
    private final AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
    public final AtomicInteger pumpCount = new AtomicInteger();
    public final SynthesizerPool.SynthesizerWrapper synthesizerWrapper;
    public final MidiFilter midiFilter;
    @Nullable
    public Sequencer sequencer;
    private final int BUFFER_SIZE = 1024;
    private final int BUFFER_COUNT = 8;
    private final InstrumentSoundInstance soundInstance;

    public MyChannel(int source, InstrumentSoundInstance soundInstance) {
        super(source);
        this.synthesizerWrapper = SynthesizerPool.getInstance().request(this);
        this.synthesizerWrapper.bindChannel(this);
        this.midiFilter = new MidiFilter(this.synthesizerWrapper.receiver, soundInstance.player);
        this.soundInstance = soundInstance;
    }

    public static MyChannel create(InstrumentSoundInstance soundInstance) {
        int[] aint = new int[1];
        AL10.alGenSources(aint);
        return new MyChannel(aint[0], soundInstance);
    }

    public void closeSequencer() {
        if(this.sequencer!=null){
            sequencer.close();
            RomanticTp.info("closing");
        }
        this.sequencer = null;
    }

    public void attachSequencer(Sequence sequence) {
        closeSequencer();
        try {
            this.sequencer = MidiSystem.getSequencer(false);
            sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.getTransmitter().setReceiver(midiFilter);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void attachBufferStream(AudioStream pStream) {
        this.streamingBufferSize = BUFFER_SIZE;
        this.pumpBuffers(BUFFER_COUNT);
    }

    @Override
    public boolean stopped() {
        return false;
    }

    @Override
    public void destroy() { //TODO: move "destroy" to other class. remove Instrument player?
        RomanticTp.LOGGER.info("c1:" + System.currentTimeMillis());
        SynthesizerPool.getInstance().delete(this);
        RomanticTp.LOGGER.info("c2:" + System.currentTimeMillis());
        this.removeProcessedBuffers();
        closeSequencer();
        InstrumentSoundManager.getInstance().remove(soundInstance.player);
        super.destroy();
    }

    @Override
    public void updateStream() {
        if (AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED) {
            this.pumpBuffers(BUFFER_COUNT);
            AL10.alSourcePlay(this.source);
            RomanticTp.LOGGER.info("YES!!!");
            return;
        }
        int i = this.removeProcessedBuffers();
        this.pumpBuffers(i);
    }

    @Override
    public void pumpBuffers(int pReadCount) {
        pumpCount.set(pReadCount);
        synchronized (pumpCount){
            if(pumpCount.get() > 0){
                pumpCount.notify();
            }
        }
    }

    public void write(byte[] b, int off, int len) { //TODO: delay?
        synchronized (pumpCount){
            if(pumpCount.get() <= 0){
                try {
                    pumpCount.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
        pumpCount.decrementAndGet();
        ByteBuffer bytebuffer = AudioLoader.convertAudioBytes(b, audioFormat.getSampleSizeInBits() == 16,
                audioFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        if (bytebuffer != null) {
            (new SoundBuffer(bytebuffer, audioFormat)).releaseAlBuffer().ifPresent((p_83669_) -> {
                AL10.alSourceQueueBuffers(this.source, new int[]{p_83669_});
            });
        }
    }

    @Override
    public void pause() {
        //TODO: pause use stop?
    }

    @Override
    public void unpause() {
    }
}
