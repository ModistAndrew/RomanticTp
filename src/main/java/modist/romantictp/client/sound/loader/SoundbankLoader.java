package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.fork.gervill.SF2Soundbank;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SoundbankLoader implements ResourceManagerReloadListener {
    @Nullable
    public SF2Soundbank soundbank;
    private static final SoundbankLoader instance = new SoundbankLoader();

    public static SoundbankLoader getInstance() {
        return instance;
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        try {
            soundbank = RomanticTpConfig.SOUNDBANK_LOCATION.get().isEmpty() ?
                    getDefaultSoundbank(pResourceManager) :
                    new SF2Soundbank(new File(RomanticTpConfig.SOUNDBANK_LOCATION.get()));
            RomanticTp.LOGGER.info("Soundbank loaded: {}", soundbank.getName());
        } catch (Exception e) {
            RomanticTp.LOGGER.warn("Failed to load soundbank. Error: ", e);
        }
    }

    private SF2Soundbank getDefaultSoundbank(ResourceManager pResourceManager) throws IOException {
        Optional<Resource> resource = pResourceManager.getResource(new ResourceLocation(RomanticTp.MODID, "soundbank/romantictp.sf3"));
        if(resource.isPresent()) {
            return new SF2Soundbank(resource.get().open());
        }
        return null;
    }
}
