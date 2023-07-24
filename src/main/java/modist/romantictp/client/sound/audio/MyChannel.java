package modist.romantictp.client.sound.audio;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.SoundBuffer;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.util.AudioHelper;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.openal.AL10;

import javax.sound.midi.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

public class MyChannel extends Channel {  //Thread safety: handling audio is OK. No direct access to Render Thread.
    public final AtomicInteger pumpCount = new AtomicInteger();
    public final SynthesizerPool.SynthesizerWrapper synthesizerWrapper;
    public final Receiver receiver; //synthesizer receiver
    private final int BUFFER_SIZE = 1024;
    private final int BUFFER_COUNT = 8;

    public MyChannel(int source) {
        super(source);
        this.synthesizerWrapper = SynthesizerPool.getInstance().request(this);
        this.synthesizerWrapper.bindChannel(this);
        this.receiver = this.synthesizerWrapper.receiver;
    }

    public static MyChannel create() {
        int[] aint = new int[1];
        AL10.alGenSources(aint);
        return new MyChannel(aint[0]);
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
    public void destroy() { //TODO: move "destroy" to other class. destroy and CLOSE; instance clear; player clear?
        SynthesizerPool.getInstance().delete(this);
        this.removeProcessedBuffers();
        //InstrumentSoundManager.getInstance().remove(soundInstance.player);
        super.destroy();
    }

    @Override
    public void updateStream() {
        if (AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED) {
            this.pumpBuffers(BUFFER_COUNT);
            AL10.alSourcePlay(this.source);
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

    public void write(byte[] b, int off, int len) { //TODO: delay and lag
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
        ByteBuffer bytebuffer = AudioHelper.convertAudioBytes(b, AudioHelper.AUDIO_FORMAT.getSampleSizeInBits() == 16,
                AudioHelper.AUDIO_FORMAT.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        if (bytebuffer != null) {
            (new SoundBuffer(bytebuffer, AudioHelper.AUDIO_FORMAT)).releaseAlBuffer().ifPresent((p_83669_) -> {
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
