package modist.romantictp.client.sound.util;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class AudioHelper {
    public static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100, 16, 1, true, false);
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
}
