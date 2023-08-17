package modist.romantictp.client.sound;

import com.mojang.blaze3d.audio.SoundBuffer;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.util.AlHelper;
import modist.romantictp.client.sound.util.AudioHelper;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

public class ALDataLine implements SourceDataLine {
    private final SourceDataLine dataLine;
    private final int source;
    public final AtomicInteger pumpCount = new AtomicInteger();

    public ALDataLine(SourceDataLine line) {
        this.dataLine = line;
        int[] aInt = new int[1];
        AL10.alGenSources(aInt);
        this.source = aInt[0];
        AlHelper.checkALError(); //TODO destroy
    }

    public void tick() {
        int i = this.removeProcessedBuffers();
        this.pumpBuffers(i);
        if (AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) { //start
            this.pumpBuffers(8);
        }
        AlHelper.checkALError();
    }

    private void pumpBuffers(int pReadCount) {
        pumpCount.set(pReadCount);
        synchronized (pumpCount) {
            if (pumpCount.get() > 0) {
                pumpCount.notify();
            }
        }
    }

    private int removeProcessedBuffers() {
        int i = AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_PROCESSED);
        if (i > 0) {
            int[] aInt = new int[i];
            AL10.alSourceUnqueueBuffers(this.source, aInt);
            AL10.alDeleteBuffers(aInt);
        }
        AlHelper.checkALError();
        return i;
    }

    @Override
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
        dataLine.open(format, bufferSize);
    }

    @Override
    public void open(AudioFormat format) throws LineUnavailableException {
        dataLine.open(format);
    }

    @Override
    public int write(byte[] b, int off, int len) {
        synchronized (pumpCount) {
            if (pumpCount.get() <= 0) {
                try {
                    pumpCount.wait();
                } catch (InterruptedException e) {
                    RomanticTp.LOGGER.info("Stopping dataLine");
                    return 0;
                }
            }
        }
        pumpCount.decrementAndGet();
        ByteBuffer bytebuffer = AudioHelper.convertAudioBytes(b, AudioHelper.AUDIO_FORMAT.getSampleSizeInBits() == 16,
                AudioHelper.AUDIO_FORMAT.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        (new SoundBuffer(bytebuffer, AudioHelper.AUDIO_FORMAT)).releaseAlBuffer().ifPresent(i -> {
            AL10.alSourceQueueBuffers(this.source, new int[]{i});
            if (AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) { //start
                AL10.alSourcePlay(this.source);
            }
            AlHelper.checkALError();
        });
        return len;
    }

    @Override
    public void drain() {
        dataLine.drain();
    }

    @Override
    public void flush() {
        dataLine.flush();
    }

    @Override
    public void start() {
        dataLine.start();
    }

    @Override
    public void stop() {
        dataLine.stop();
    }

    @Override
    public boolean isRunning() {
        return dataLine.isRunning();
    }

    @Override
    public boolean isActive() {
        return dataLine.isActive();
    }

    @Override
    public AudioFormat getFormat() {
        return dataLine.getFormat();
    }

    @Override
    public int getBufferSize() {
        return dataLine.getBufferSize();
    }

    @Override
    public int available() {
        return dataLine.available();
    }

    @Override
    public int getFramePosition() {
        return dataLine.getFramePosition();
    }

    @Override
    public long getLongFramePosition() {
        return dataLine.getFramePosition();
    }

    @Override
    public long getMicrosecondPosition() {
        return dataLine.getMicrosecondPosition();
    }

    @Override
    public float getLevel() {
        return dataLine.getLevel();
    }

    @Override
    public Line.Info getLineInfo() {
        return dataLine.getLineInfo();
    }

    @Override
    public void open() throws LineUnavailableException {
        dataLine.open();
    }

    @Override
    public void close() {
        dataLine.close();
    }

    @Override
    public boolean isOpen() {
        return dataLine.isOpen();
    }

    @Override
    public Control[] getControls() {
        return dataLine.getControls();
    }

    @Override
    public boolean isControlSupported(Control.Type control) {
        return dataLine.isControlSupported(control);
    }

    @Override
    public Control getControl(Control.Type control) {
        return dataLine.getControl(control);
    }

    @Override
    public void addLineListener(LineListener listener) {
        dataLine.addLineListener(listener);
    }

    @Override
    public void removeLineListener(LineListener listener) {
        dataLine.addLineListener(listener);
    }
}
