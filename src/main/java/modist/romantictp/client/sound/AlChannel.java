package modist.romantictp.client.sound;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.SoundBuffer;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.client.sound.util.AlHelper;
import modist.romantictp.client.sound.util.AudioHelper;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.openal.AL10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AlChannel extends Channel {  //used for AlDataLine to pass data to openAL. ticked by vanilla soundEngine, but simplified.
    private final AtomicInteger pumpCount = new AtomicInteger();
    private final int BUFFER_COUNT = 8;
    private final AtomicBoolean alive = new AtomicBoolean(true);

    public AlChannel(int source) {
        super(source);
    }

    public static AlChannel create() {
        int[] aint = new int[1];
        AL10.alGenSources(aint);
        AlHelper.checkALError();
        return new AlChannel(aint[0]);
    }

    @Override
    public void attachBufferStream(AudioStream pStream) {
        this.pumpBuffers(BUFFER_COUNT);
    }

    @Override
    public boolean stopped() {
        return !this.alive.get(); //only can be stopped by destroy
    }

    @Override
    public void destroy() { //called in stopAll
        if (this.alive.compareAndSet(true, false)) { //avoid duplication
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
        } else {
            int i = this.removeProcessedBuffers();
            this.pumpBuffers(i);
            AlHelper.checkALError();
        }
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
                    return;
                }
            }
        }
        pumpCount.decrementAndGet();
        ByteBuffer bytebuffer = AudioHelper.convertAudioBytes(b, AudioHelper.AUDIO_FORMAT.getSampleSizeInBits() == 16,
                AudioHelper.AUDIO_FORMAT.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        if (!this.stopped()) {
            (new SoundBuffer(bytebuffer, AudioHelper.AUDIO_FORMAT)).releaseAlBuffer().ifPresent((p_83669_) -> {
                if (!this.stopped()) {
                    AL10.alSourceQueueBuffers(this.source, new int[]{p_83669_});
                    AlHelper.checkALError();
                }
            });
        }
    }

    @Override
    public void pause() {
        //controlled by instance. to avoid sound delay, do nothing here
    }

    @Override
    public void unpause() {
        //controlled by instance. to avoid sound delay, do nothing here
    }

    public void setReverb(ReverbType reverbType) {
        EFXManager.getInstance().applyEFX(reverbType, source);
    }
}
