package modist.romantictp.client.sound;

import modist.romantictp.client.sound.efx.ReverbType;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.*;
import java.util.concurrent.CompletableFuture;

public class AlDataLine implements SourceDataLine {
    private final SourceDataLine dataLine;
    private volatile CompletableFuture<AlChannel> channel = new CompletableFuture<>();

    public AlDataLine(SourceDataLine line) {
        this.dataLine = line;
    }

    public void bindChannel(@Nullable AlChannel channel) { //set to null to clear
        if(channel==null) {
            if(this.channel.isDone()) {
                this.channel.join().destroy(); //channel may still be alive when instance is destroyed (but not vice versa!)
            }
            this.channel = new CompletableFuture<>();
        } else {
            this.channel.complete(channel);
        }
    }

    public void setReverb(ReverbType reverb) { //may have not been completed
        channel.thenAcceptAsync(channel -> channel.setReverb(reverb));
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
        if (!channel.isDone() || channel.join().stopped()) {
            try {
                Thread.sleep(10);
                return 0;
            } catch (InterruptedException e) {
                return 0;
            }
        }
        AlChannel alChannel = channel.join();
        alChannel.write(b, off, len);
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
