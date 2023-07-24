package modist.romantictp.client.sound.util;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class MidiHelper {
    public static MidiMessage makeMessage(int command, int channel, int note, int velocity) {
        ShortMessage message;
        try {
            message = new ShortMessage(command, channel, note, velocity);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    public static MidiMessage startMessage(int channel, int note, int velocity){
        return makeMessage(ShortMessage.NOTE_ON, channel, note, velocity);
    }

    public static MidiMessage stopMessage(int channel, int note){
        return makeMessage(ShortMessage.NOTE_OFF, channel, note, 0);
    }

    public static MidiMessage instrumentMessage(int channel, int instrument){
        return makeMessage(ShortMessage.PROGRAM_CHANGE, channel, instrument, 0);
    }
}
