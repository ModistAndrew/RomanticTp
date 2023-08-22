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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class SoundbankLoader implements ResourceManagerReloadListener {
    @Nullable
    public SF2Soundbank soundbank;
    private static final SoundbankLoader instance = new SoundbankLoader();

    public static SoundbankLoader getInstance() {
        return instance;
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        String loc = RomanticTpConfig.SOUNDBANK.get();
        try {
            soundbank = getSoundbank(pResourceManager, loc);
            if (soundbank == null) {
                soundbank = new SF2Soundbank(new File(loc)); //shouldn't be null
            }
            RomanticTp.LOGGER.info("Soundbank {} loaded, name: {}", loc, soundbank.getName());
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                RomanticTp.LOGGER.warn("Cannot find soundbank {}. Use fallback soundbank.", loc);
            } else {
                RomanticTp.LOGGER.warn("Failed to load soundbank {}. Use fallback soundbank. Error: ", loc, e);
            }
        }
    }

    private SF2Soundbank getSoundbank(ResourceManager pResourceManager, String name) throws IOException {
        Map<ResourceLocation, Resource> resourceMap =
                pResourceManager.listResources("soundbank", l -> l.getPath().endsWith(name));
        return resourceMap.isEmpty() ? null : new SF2Soundbank(resourceMap.values().iterator().next().open());
    }
}
