package modist.romantictp;

import com.mojang.blaze3d.audio.OggAudioStream;
import modist.romantictp.client.sound.fork.gervill.ModelByteBuffer;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteBuffer;

public class OggTest {

    public static void main(String[] args) throws IOException, LineUnavailableException {
        File file = new File("C:\\Users\\zjx\\Desktop\\Music\\sound\\trumpet.ogg");
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        OggAudioStream ogg =
                new OggAudioStream(new AudioInputStream(file.toURL().openStream(), format, file.length()));
        ByteBuffer buf = ogg.readAll();
        byte[] arr = new byte[buf.remaining()];
        buf.get(arr);
        ByteArrayInputStream stream = new ByteArrayInputStream(arr);
        SourceDataLine sourceLine = AudioSystem.getSourceDataLine(format);
        sourceLine.open(format);
        sourceLine.start();
        int bytesRead = 0;
        byte[] buffer = new byte[4096];
        while (bytesRead != -1) {
            bytesRead = stream.read(buffer, 0, buffer.length);
            if (bytesRead >= 0) {
                sourceLine.write(buffer, 0, bytesRead);
            }
        }
        sourceLine.drain();
        sourceLine.close();
    }
}
