package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.AlDataLine;
import modist.romantictp.client.sound.fork.gervill.SoftSynthesizer;
import modist.romantictp.client.sound.util.AudioHelper;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.sampled.*;
import java.util.*;
import java.util.concurrent.*;

public class SynthesizerPool implements ResourceManagerReloadListener {
    public final List<SynthesizerWrapper> availableSynthesizers = Collections.synchronizedList(new ArrayList<>());
    private static final SynthesizerPool instance = new SynthesizerPool();

    public void init() {
        availableSynthesizers.clear();
        for (int i = 0; i < RomanticTpConfig.INITIAL_SYNTHESIZER_COUNT.get(); i++) {
            create();
        }
    }

    public static SynthesizerPool getInstance() {
        return instance;
    }

    public SynthesizerWrapper request() {
        create();
        return availableSynthesizers.isEmpty() ?
                SynthesizerWrapper.create() : availableSynthesizers.remove(availableSynthesizers.size() - 1);
    }

    public void delete(SynthesizerWrapper wrapper) {
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            RomanticTp.LOGGER.info("Start deleting synthesizer {}", availableSynthesizers.size());
            wrapper.close();
            RomanticTp.LOGGER.info("Finish deleting synthesizer {}", availableSynthesizers.size());
        });
    }

    private void create() {
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            RomanticTp.LOGGER.info("Start creating synthesizer {}", availableSynthesizers.size());
            availableSynthesizers.add(SynthesizerWrapper.create());
            RomanticTp.LOGGER.info("Finish creating synthesizer {}", availableSynthesizers.size());
        });
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        SoundbankLoader.getInstance().onResourceManagerReload(pResourceManager); //first load soundbank
        init();
    }

    public record SynthesizerWrapper(SoftSynthesizer synthesizer, Receiver receiver, AlDataLine dataLine) {

        public static SynthesizerWrapper create() {
            try {
                SoftSynthesizer synthesizer = new SoftSynthesizer();
                AudioFormat audioFormat = AudioHelper.AUDIO_FORMAT;
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
                AlDataLine alDataLine = new AlDataLine(sourceDataLine);
                Map<String, Object> params = new HashMap<>();
                params.put("jitter correction", RomanticTpConfig.JITTER_CORRECTION.get());
                synthesizer.open(alDataLine, params);
                if (SoundbankLoader.getInstance().soundbank != null) {
                    synthesizer.loadAllInstruments(SoundbankLoader.getInstance().soundbank);
                }
                return new SynthesizerWrapper(synthesizer, synthesizer.getReceiver(), alDataLine);
            } catch (MidiUnavailableException | LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            this.synthesizer.close();
        }
    }
}
