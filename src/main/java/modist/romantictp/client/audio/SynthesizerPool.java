package modist.romantictp.client.audio;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.audio.fork.sound.AudioSynthesizer;
import modist.romantictp.client.audio.fork.sound.SoftSynthesizer;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.sampled.*;
import java.util.*;
import java.util.concurrent.*;

public class SynthesizerPool {
    public volatile List<SynthesizerWrapper> availableSynthesizers = Collections.synchronizedList(new ArrayList<>());
    private static final SynthesizerPool instance = new SynthesizerPool();

    private static final int INITIAL_COUNT = 4;
    public void init() {
        for(int i=0; i<INITIAL_COUNT; i++){
            create();
        }
    }

    public static SynthesizerPool getInstance(){
        return instance;
    }

    public SynthesizerWrapper request(MyChannel channel) {
        create();
        return availableSynthesizers.isEmpty() ?
                new SynthesizerWrapper() : availableSynthesizers.remove(availableSynthesizers.size()-1);
    }

    public void delete(MyChannel channel) {
        CompletableFuture.runAsync(() -> {
            RomanticTp.info("deletingSyn");
            channel.synthesizerWrapper.close();
            RomanticTp.info("deleteFinish");
        });
    }

    private void create() {
        RomanticTp.info("syn" + availableSynthesizers.size());
        CompletableFuture.runAsync(() -> {
            availableSynthesizers.add(new SynthesizerWrapper());
            RomanticTp.info("syn" + availableSynthesizers.size());
        });
    }

    public static class SynthesizerWrapper {
        public final AudioSynthesizer synthesizer;
        public final MyDataLine dataLine;
        public final Receiver receiver;
        public SynthesizerWrapper() {
            this.synthesizer = new SoftSynthesizer();

            AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
            DataLine.Info info1 = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine;
            try {
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(info1);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }

            this.dataLine = new MyDataLine(sourceDataLine);
            try {
                this.synthesizer.open(this.dataLine, null);
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }

            if(SoundbankLoader.getInstance().soundbank != null) {
                synthesizer.loadAllInstruments(SoundbankLoader.getInstance().soundbank);
            }

            try {
                this.receiver = this.synthesizer.getReceiver();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }
        }

        public void bindChannel(MyChannel channel){
            this.dataLine.bindChannel(channel);
        }

        public void close(){
            this.synthesizer.close();
        }
    }
}
