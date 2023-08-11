package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.fork.gervill.SF2Soundbank;
import modist.romantictp.client.sound.fork.gervill.SF2SoundbankReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.annotation.Nullable;
import javax.sound.midi.InvalidMidiDataException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
                        new SF2Soundbank(pResourceManager.getResource(new ResourceLocation(RomanticTp.MODID, "soundbank/romantictp.sf3")).get().open()) :
                        new SF2Soundbank(new File(RomanticTpConfig.SOUNDBANK_LOCATION.get()));
                RomanticTp.LOGGER.info("Soundbank loaded: {}", SoundbankLoader.getInstance().soundbank.getName());
            } catch(Exception e) {
                RomanticTp.LOGGER.warn("Failed to load soundbank. Error: ", e);
            }
        }
}
