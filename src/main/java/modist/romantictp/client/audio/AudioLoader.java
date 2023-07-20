package modist.romantictp.client.audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class AudioLoader {
    public int id;
    public AudioInputStream audioInputStream;

    public AudioLoader(AudioInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }

    public int getAlBuffer() {
        this.id = AL10.alGenBuffers();
        addBufferData();
        return this.id;
    }

    public void addBufferData() {
        AudioFormat audioformat = audioInputStream.getFormat();
        int channels = 0;
        if (audioformat.getChannels() == 1) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = AL11.AL_FORMAT_MONO8;
            } else if (audioformat.getSampleSizeInBits() == 16) {
                channels = AL11.AL_FORMAT_MONO16;
            } else {
                assert false : "Illegal sample size";
            }
        } else if (audioformat.getChannels() == 2) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = AL11.AL_FORMAT_STEREO8;
            } else if (audioformat.getSampleSizeInBits() == 16) {
                channels = AL11.AL_FORMAT_STEREO16;
            } else {
                assert false : "Illegal sample size";
            }
        } else {
            assert false : "Only mono or stereo is supported";
        }

        ByteBuffer buffer;
        try {
            byte[] buf = new byte[audioInputStream.available()];
            int read, total = 0;
            while ((read = audioInputStream.read(buf, total, buf.length - total)) != -1 && total < buf.length) {
                total += read;
            }
            buffer = convertAudioBytes(
                    buf,
                    audioformat.getSampleSizeInBits() == 16,
                    audioformat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            AL10.alBufferData(id, channels, buffer, (int) audioformat.getSampleRate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteBuffer convertAudioBytes(byte[] audio_bytes, boolean two_bytes_data, ByteOrder order) {
        ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
        dest.order(ByteOrder.nativeOrder());
        ByteBuffer src = ByteBuffer.wrap(audio_bytes);
        src.order(order);
        if (two_bytes_data) {
            ShortBuffer dest_short = dest.asShortBuffer();
            ShortBuffer src_short = src.asShortBuffer();
            while (src_short.hasRemaining()) dest_short.put(src_short.get());
        } else {
            while (src.hasRemaining()) dest.put(src.get());
        }
        dest.rewind();
        return dest;
    }

    public static ByteBuffer loadBuffer(AudioInputStream stream) {
        AudioFormat audioformat = stream.getFormat();
        int channels = 0;
        if (audioformat.getChannels() == 1) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = AL11.AL_FORMAT_MONO8;
            } else if (audioformat.getSampleSizeInBits() == 16) {
                channels = AL11.AL_FORMAT_MONO16;
            } else {
                assert false : "Illegal sample size";
            }
        } else if (audioformat.getChannels() == 2) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = AL11.AL_FORMAT_STEREO8;
            } else if (audioformat.getSampleSizeInBits() == 16) {
                channels = AL11.AL_FORMAT_STEREO16;
            } else {
                assert false : "Illegal sample size";
            }
        } else {
            assert false : "Only mono or stereo is supported";
        }

        ByteBuffer buffer;
        try {
            byte[] buf = new byte[stream.available()];
            int read, total = 0;
            while ((read = stream.read(buf, total, buf.length - total)) != -1 && total < buf.length) {
                total += read;
            }
            buffer = convertAudioBytes(
                    buf,
                    audioformat.getSampleSizeInBits() == 16,
                    audioformat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            return buffer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
