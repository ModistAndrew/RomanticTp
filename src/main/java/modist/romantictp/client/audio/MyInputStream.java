package modist.romantictp.client.audio;

import modist.romantictp.RomanticTp;
import net.minecraft.client.sounds.AudioStream;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MyInputStream implements AudioStream {
    private final AudioFormat audioFormat;
    private final InputStream stream;

    public MyInputStream(InputStream stream) {
        this.stream = stream;
        this.audioFormat = new AudioFormat(44100, 16, 2, true, false);
    }

    @Override
    public AudioFormat getFormat() {
        return audioFormat;
    }

    @Override
    public ByteBuffer read(int pSize) {
        try {
            RomanticTp.LOGGER.info("reading" + pSize + "available" + stream.available());
            byte[] buf = new byte[pSize];
            int read, total = 0;
            while ((read = stream.read(buf, total, buf.length - total)) != -1
                    && total < buf.length) {
                total += read;
            }
            RomanticTp.LOGGER.info("readingFinish" + pSize);
            return AudioLoader.convertAudioBytes(
                    buf,
                    audioFormat.getSampleSizeInBits() == 16,
                    audioFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
    }
}
