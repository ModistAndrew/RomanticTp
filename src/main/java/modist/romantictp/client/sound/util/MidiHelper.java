package modist.romantictp.client.sound.util;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MidiHelper {
    public static ShortMessage message(int command, int note, int velocity) {
        ShortMessage message;
        try {
            message = new ShortMessage(command, 1, note, velocity);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    public static ShortMessage startMessage(int note, int velocity){
        return message(ShortMessage.NOTE_ON, clip(note), clip(velocity));
    }

    public static ShortMessage stopMessage(int note, int velocity){
        return message(ShortMessage.NOTE_OFF, clip(note), clip(velocity));
    }

    public static ShortMessage stopMessage(int note){
        return stopMessage(note, 0);
    }

    public static ShortMessage instrumentMessage(int instrument){
        return message(ShortMessage.PROGRAM_CHANGE, instrument, 0);
    }

    private static int clip(int value) {
        return Math.max(0, Math.min(127, value));
    }

    public static void save(FriendlyByteBuf buf, ShortMessage message){
        buf.writeInt(message.getCommand());
        buf.writeInt(message.getData1());
        buf.writeInt(message.getData2());
    }

    public static ShortMessage load(FriendlyByteBuf buf){
        int command = buf.readInt();
        int data1 = buf.readInt();
        int data2 = buf.readInt();
        return message(command, data1, data2);
    }

    @Nullable
    public static Sequence loadSequence(byte[] data){
        try {
            return data.length>0 ? MidiSystem.getSequence(new ByteArrayInputStream(data)) : null;
        } catch (InvalidMidiDataException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
