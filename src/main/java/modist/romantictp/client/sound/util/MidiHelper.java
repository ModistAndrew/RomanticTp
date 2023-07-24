package modist.romantictp.client.sound.util;

import org.lwjgl.system.MathUtil;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class MidiHelper {
    public static MidiMessage message(int command, int note, int velocity) {
        ShortMessage message;
        try {
            message = new ShortMessage(command, 1, note, velocity);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    public static MidiMessage startMessage(int note, int velocity){
        return message(ShortMessage.NOTE_ON, clip(note), clip(velocity));
    }

    public static MidiMessage stopMessage(int note){
        return message(ShortMessage.NOTE_OFF, clip(note), 0);
    }

    public static MidiMessage instrumentMessage(int instrument){
        return message(ShortMessage.PROGRAM_CHANGE, instrument, 0);
    }

    private static int clip(int value) {
        return Math.max(0, Math.min(127, value));
    }
}
