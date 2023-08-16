package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.InstrumentSoundInstance;
import modist.romantictp.client.sound.fork.gervill.AudioSynthesizer;
import modist.romantictp.client.sound.fork.gervill.SoftSynthesizer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;
import java.util.*;
import java.util.concurrent.*;

public class SynthesizerPool implements ResourceManagerReloadListener {
    public final List<SynthesizerWrapper> availableSynthesizers = Collections.synchronizedList(new ArrayList<>());
    private static final SynthesizerPool instance = new SynthesizerPool();

    private static final int INITIAL_COUNT = 4;

    public void init() {
        availableSynthesizers.clear();
        for (int i = 0; i < INITIAL_COUNT; i++) {
            create();
        }
    }

    public static SynthesizerPool getInstance() {
        return instance;
    }

    public SynthesizerWrapper request(InstrumentSoundInstance instance) {
        create();
        return availableSynthesizers.isEmpty() ?
                new SynthesizerWrapper() : availableSynthesizers.remove(availableSynthesizers.size() - 1);
    }

    public void delete(InstrumentSoundInstance instance) {
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            RomanticTp.LOGGER.info("Start deleting synthesizer {}", availableSynthesizers.size());
            instance.synthesizerWrapper.close();
            RomanticTp.LOGGER.info("Finish deleting synthesizer {}", availableSynthesizers.size());
        });
    }

    private void create() {
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            RomanticTp.LOGGER.info("Start creating synthesizer {}", availableSynthesizers.size());
            availableSynthesizers.add(new SynthesizerWrapper());
            RomanticTp.LOGGER.info("Finish creating synthesizer {}", availableSynthesizers.size());
        });
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        SoundbankLoader.getInstance().onResourceManagerReload(pResourceManager); //first load soundbank
        init();
    }

    public static class SynthesizerWrapper {
        public final Synthesizer synthesizer;
        public final Receiver receiver;

        public SynthesizerWrapper() {
            try {
                this.synthesizer = new SoftSynthesizer();
                synthesizer.open();
                if (SoundbankLoader.getInstance().soundbank != null) {
                    synthesizer.loadAllInstruments(SoundbankLoader.getInstance().soundbank);
                }
                this.receiver = this.synthesizer.getReceiver();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            this.synthesizer.close();
        }
    }
}
