package modist.romantictp;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MidiPlayer {

    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException,
            IOException, UnsupportedAudioFileException, LineUnavailableException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MidiPlayer player = new MidiPlayer();
        player.setUpPlayer(15);
    }

    public void setUpPlayer(int numOfNotes) throws MidiUnavailableException, IOException, InvalidMidiDataException, UnsupportedAudioFileException, LineUnavailableException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        Soundbank soundbank = MidiSystem.getSoundbank(new File("C:/Users/zjx/Desktop/Music/Touhou1.sf2"));
        synthesizer.loadAllInstruments(soundbank);

        Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.open();
        Sequence sequence = new Sequence(Sequence.PPQ, 4);
        Track track = sequence.createTrack();
        for (int i = 70; i < 75; i += 4) {
            track.add(makeEvent(192, 0, 10, 100, i-70));
            track.add(makeEvent(144, 0, i, 100, i-70));
            track.add(makeEvent(128, 0, i, 100, i-70 + 2));
        }
        sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
        sequencer.setSequence(sequence);
        sequencer.start();
        while(true){
            if(!sequencer.isRunning()){
                System.exit(0);
            }
        }

        /*ByteArrayOutputStream out = new ByteArrayOutputStream();
        MidiSystem.write(sequence, 1, out);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(out.toByteArray()));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        // Play clip
        clip.start();

        while(true){

        }*/
    }

    public MidiEvent makeEvent(int command, int channel,
                               int note, int velocity, int tick) throws InvalidMidiDataException {
        MidiEvent event;
        ShortMessage a = new ShortMessage();
        a.setMessage(command, channel, note, velocity);
        event = new MidiEvent(a, tick);
        return event;
    }
}