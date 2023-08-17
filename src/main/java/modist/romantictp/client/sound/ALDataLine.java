package modist.romantictp.client.sound;

import javax.sound.sampled.*;

public class ALDataLine implements SourceDataLine {
    private final SourceDataLine dataLine;

    public ALDataLine(SourceDataLine line) {
        this.dataLine = line;
    }

    @Override
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
        dataLine.open(format, bufferSize);
    }

    @Override
    public void open(AudioFormat format) throws LineUnavailableException {
        dataLine.open(format);
    }

    public void tick() {

    }

    @Override
    public int write(byte[] b, int off, int len) {
        return dataLine.write(b, off, len);
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
