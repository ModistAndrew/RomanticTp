package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.InstrumentSoundManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class SynthesizerPool implements ResourceManagerReloadListener {
    public final Stack<SynthesizerWrapper> availableSynthesizers;
    private static final SynthesizerPool instance = new SynthesizerPool();

    public SynthesizerPool() {
        this.availableSynthesizers = new Stack<>();
    }

    public static SynthesizerPool getInstance() {
        return instance;
    }

    public void init() {
        InstrumentSoundManager.getInstance().stopAll();
        int all = availableSynthesizers.size();
        for(int i=0; i<all; i++){
            RomanticTp.LOGGER.debug("Start deleting synthesizer, current: {}", availableSynthesizers.size());
            availableSynthesizers.pop().delete();
            RomanticTp.LOGGER.debug("Finish deleting synthesizer, current: {}", availableSynthesizers.size());
        }
        for (int i = 0; i < RomanticTpConfig.SYNTHESIZER_POOL_SIZE.get(); i++) {
            RomanticTp.LOGGER.debug("Start creating synthesizer, current: {}", availableSynthesizers.size());
            SynthesizerWrapper synthesizerWrapper = SynthesizerWrapper.create();
            if(synthesizerWrapper != null) {
                availableSynthesizers.push(synthesizerWrapper);
            }
            RomanticTp.LOGGER.debug("Finish creating synthesizer, current: {}", availableSynthesizers.size());
        }
    }

    @Nullable
    public SynthesizerWrapper request() {
        RomanticTp.LOGGER.debug("Start attaching synthesizer, current: {}", availableSynthesizers.size());
        SynthesizerWrapper ret = availableSynthesizers.isEmpty() ?
                null : availableSynthesizers.pop();
        RomanticTp.LOGGER.debug("Finish attaching synthesizer, current: {}", availableSynthesizers.size());
        return ret;
    }

    public void free(SynthesizerWrapper wrapper) {
        RomanticTp.LOGGER.debug("Start freeing synthesizer, current: {}", availableSynthesizers.size());
        wrapper.free();
        availableSynthesizers.push(wrapper);
        RomanticTp.LOGGER.debug("Finish freeing synthesizer, current: {}", availableSynthesizers.size());
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        SoundbankLoader.getInstance().onResourceManagerReload(pResourceManager); //first load soundbank
        init();
    }
}
