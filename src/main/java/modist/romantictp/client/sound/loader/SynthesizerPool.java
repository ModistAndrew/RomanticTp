package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.audio.MyChannel;
import modist.romantictp.client.sound.audio.MyDataLine;
import modist.romantictp.client.sound.fork.gervill.AudioSynthesizer;
import modist.romantictp.client.sound.fork.gervill.SF2Soundbank;
import modist.romantictp.client.sound.fork.gervill.SoftSynthesizer;
import modist.romantictp.client.sound.util.AudioHelper;
import modist.romantictp.client.sound.util.FileHelper;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.annotation.Nullable;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.sampled.*;
import java.util.*;
import java.util.concurrent.*;

public class SynthesizerPool implements ResourceManagerReloadListener {
    public volatile List<SynthesizerWrapper> availableSynthesizers = Collections.synchronizedList(new ArrayList<>());
    private static final SynthesizerPool instance = new SynthesizerPool();

    private static final int INITIAL_COUNT = 4;
    public void init() {
        availableSynthesizers.clear();
        for(int i=0; i<INITIAL_COUNT; i++){
            create();
        }
    }

    public static SynthesizerPool getInstance() {
        return instance;
    }

    public SynthesizerWrapper request(MyChannel channel) {
        create();
        return availableSynthesizers.isEmpty() ?
                new SynthesizerWrapper() : availableSynthesizers.remove(availableSynthesizers.size()-1);
    }

    public void delete(MyChannel channel) {
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            RomanticTp.info("deletingSyn");
            channel.synthesizerWrapper.close();
            RomanticTp.info("deleteFinish");
        });
    }

    private void create() {
        RomanticTp.info("syn" + availableSynthesizers.size());
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            RomanticTp.info(FileHelper.class.getResource("/"));
            availableSynthesizers.add(new SynthesizerWrapper());
            RomanticTp.info("syn" + availableSynthesizers.size());
        });
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        SoundbankLoader.getInstance().onResourceManagerReload(pResourceManager); //first load soundbank
        if(SoundbankLoader.getInstance().soundbank!=null) {
            RomanticTp.info("Soundbank loaded: " + SoundbankLoader.getInstance().soundbank.getName());
        }
        init();
    }

    public static class SynthesizerWrapper { //TODO: delay? jitter correction?
        public final AudioSynthesizer synthesizer;
        public final MyDataLine dataLine;
        public final Receiver receiver;
        public SynthesizerWrapper() {
            this.synthesizer = new SoftSynthesizer();

            AudioFormat audioFormat = AudioHelper.AUDIO_FORMAT;
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
