package modist.romantictp.client.sound;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.SoundBuffer;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.client.sound.loader.SynthesizerPool;
import modist.romantictp.client.sound.util.AlHelper;
import modist.romantictp.client.sound.util.AudioHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.openal.AL10;

import javax.sound.midi.*;
import javax.sound.sampled.FloatControl;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MyChannel extends Channel {  //Thread safety: handling audio is OK. No direct access to Render Thread.
    public final AtomicInteger pumpCount = new AtomicInteger();
    public final SynthesizerPool.SynthesizerWrapper synthesizerWrapper;
    public final Receiver receiver; //synthesizer receiver
    private final int BUFFER_SIZE = 1024;
    private final int BUFFER_COUNT = 8;
    private ReverbType reverb = ReverbType.GENERIC;
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private CompletableFuture<InstrumentSoundInstance> instance = new CompletableFuture<>();

    public MyChannel(int source) {
        super(source);
        this.synthesizerWrapper = SynthesizerPool.getInstance().request(this);
        this.synthesizerWrapper.bindChannel(this);
        this.receiver = this.synthesizerWrapper.receiver;
    }

    public void setReverb(ReverbType reverb) {
        this.reverb = reverb;
        EFXManager.getInstance().applyEFX(this.reverb, this.source);
    }

    public void bindInstance(InstrumentSoundInstance instance) {
        this.instance.complete(instance);
    }

    public static MyChannel create() {
        int[] aint = new int[1];
        AL10.alGenSources(aint);
        AlHelper.checkALError();
        return new MyChannel(aint[0]);
    }

    @Override
    public void attachBufferStream(AudioStream pStream) {
        this.streamingBufferSize = BUFFER_SIZE;
        this.pumpBuffers(BUFFER_COUNT);
    }

    @Override
    public boolean stopped() {
        return !this.alive.get(); //only can be stopped manually
    }

    @Override
    public void destroy() { //called outer?
        if (this.alive.compareAndSet(true, false)) { //avoid duplication
            SynthesizerPool.getInstance().delete(this); //closing synthesizer, thus closing receiver and dataLine
            this.removeProcessedBuffers(); //stream is null, super will skip
            super.destroy();
        }
    }

    @Override
    public void updateStream() {
        if (this.stopped()) {
            return;
        }
        if (AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED) {
            this.pumpBuffers(BUFFER_COUNT);
            AL10.alSourcePlay(this.source);
            AlHelper.checkALError();
            return;
        }
        int i = this.removeProcessedBuffers();
        this.pumpBuffers(i);
        AlHelper.checkALError();
    }

    @Override
    public void pumpBuffers(int pReadCount) {
        pumpCount.set(pReadCount);
        synchronized (pumpCount) {
            if (pumpCount.get() > 0) {
                pumpCount.notify();
            }
        }
    }

    public void write(byte[] b, int off, int len) {
        synchronized (pumpCount) {
            if (pumpCount.get() <= 0) {
                try {
                    pumpCount.wait();
                } catch (InterruptedException e) {
                    RomanticTp.LOGGER.info("Stopping pusher");
                    return;
                }
            }
        }
        pumpCount.decrementAndGet();
        ByteBuffer bytebuffer = AudioHelper.convertAudioBytes(b, AudioHelper.AUDIO_FORMAT.getSampleSizeInBits() == 16,
                AudioHelper.AUDIO_FORMAT.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        if (bytebuffer != null && !this.stopped()) {
            (new SoundBuffer(bytebuffer, AudioHelper.AUDIO_FORMAT)).releaseAlBuffer().ifPresent((p_83669_) -> {
                if (!this.stopped()) {
                    AL10.alSourceQueueBuffers(this.source, new int[]{p_83669_});
                    AlHelper.checkALError();
                }
            });
        }
    }

    @Override
    public void pause(){
        super.pause();
        executeOnInstance(InstrumentSoundInstance::pause);
    }

    @Override
    public void unpause(){
        super.unpause();
        executeOnInstance(InstrumentSoundInstance::unpause);
    }

    private void executeOnInstance(Consumer<InstrumentSoundInstance> consumer) { //thread safety
        instance.thenAcceptAsync(instance -> Minecraft.getInstance().execute(() -> consumer.accept(instance)));
    }
}
