package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.config.RomanticTpConfig;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SynthesizerPool implements ResourceManagerReloadListener {
    public final List<SynthesizerWrapper> availableSynthesizers;
    private static final SynthesizerPool instance = new SynthesizerPool();
    private boolean initialized = false;

    public SynthesizerPool() {
        this.availableSynthesizers = Collections.synchronizedList(new ArrayList<>());
    }

    public static SynthesizerPool getInstance() {
        return instance;
    }

    public void init() {
        initialized = true;
        for (int i = 0; i < RomanticTpConfig.SYNTHESIZER_POOL_SIZE.get(); i++) {
            create();
        }
    }

    private void create() {
        RomanticTp.LOGGER.info("Start creating synthesizer, current: {}", availableSynthesizers.size());
        availableSynthesizers.add(SynthesizerWrapper.create());
        RomanticTp.LOGGER.info("Finish creating synthesizer, current: {}", availableSynthesizers.size());
    }

    @Nullable
    public SynthesizerWrapper request() {
        RomanticTp.LOGGER.info("Attaching synthesizer, left: {}", availableSynthesizers.size());
        return availableSynthesizers.isEmpty() ?
                null : availableSynthesizers.remove(availableSynthesizers.size() - 1);
    }

    public void free(SynthesizerWrapper wrapper) {
        RomanticTp.LOGGER.info("Start freeing synthesizer, current: {}", availableSynthesizers.size());
        wrapper.free();
        availableSynthesizers.add(wrapper);
        RomanticTp.LOGGER.info("Finish freeing synthesizer, current: {}", availableSynthesizers.size());
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) { //TODO: reload this and midiKeyBoard
        if(!initialized) {
            SoundbankLoader.getInstance().onResourceManagerReload(pResourceManager); //first load soundbank
            init();
        }
    }
}
