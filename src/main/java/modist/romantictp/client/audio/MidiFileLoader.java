package modist.romantictp.client.audio;

import modist.romantictp.client.audio.fork.sound.SF2Soundbank;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MidiFileLoader {
    public HashMap<String, Sequence> resourceMap = new HashMap<>();

    private static final MidiFileLoader instance = new MidiFileLoader();

    public static MidiFileLoader getInstance() {
        return instance;
    }

    public void init() {
        try {
            Sequence sequence = MidiSystem.getSequence(new File("C:\\Users\\zjx\\Desktop\\Music\\zjx.mid"));
            resourceMap.put("default", sequence);
        } catch (IOException | InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public Sequence getSequence(String name) {
        return resourceMap.get(name) == null ? resourceMap.get("default") : resourceMap.get(name);
    }
}
