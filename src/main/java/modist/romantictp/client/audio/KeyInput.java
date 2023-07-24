package modist.romantictp.client.audio;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.List;

import modist.romantictp.client.audio.fork.sound.AudioSynthesizer;
import modist.romantictp.client.audio.fork.sound.SF2Soundbank;
import modist.romantictp.client.audio.fork.sound.SoftSynthesizer;
@Deprecated
public class KeyInput {

    /*public static PipedInputStream getStream() throws LineUnavailableException, InvalidMidiDataException, IOException, MidiUnavailableException {
        AudioSynthesizer synthesizer = new SoftSynthesizer();
        AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);

        DataLine.Info info1 = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info1);

        PipedInputStream pipedInputStream = new PipedInputStream();
        synthesizer.open(new MyDataLine(sourceDataLine, pipedInputStream), null);

        SF2Soundbank soundbank = new SF2Soundbank(new File("C:\\Users\\zjx\\Desktop\\Music\\Touhou1.sf2"));
        synthesizer.loadAllInstruments(soundbank);

        MidiDevice device;
        MidiDevice.Info[] infoList = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infoList) {
            try {
                device = MidiSystem.getMidiDevice(info);
                System.out.println(info);
                List<Transmitter> transmitters = device.getTransmitters();

                for (Transmitter transmitter : transmitters) {
                    transmitter.setReceiver(synthesizer.getReceiver());
                }

                Transmitter trans = device.getTransmitter();
                trans.setReceiver(synthesizer.getReceiver());

                device.open();
                System.out.println(device.getDeviceInfo() + " Was Opened");

            } catch (MidiUnavailableException ignored) {
            }
        }
        return pipedInputStream;
    }*/
}
