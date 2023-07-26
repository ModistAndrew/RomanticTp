package modist.romantictp.client.sound.loader;

import modist.romantictp.client.sound.fork.gervill.SF2Soundbank;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.annotation.Nullable;
import java.io.IOException;

public class SoundbankLoader implements ResourceManagerReloadListener {
    @Nullable
    public SF2Soundbank soundbank;
    private static final SoundbankLoader instance = new SoundbankLoader();
    public static SoundbankLoader getInstance() {
        return instance;
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) { //TODO: config
        pResourceManager.listResources("soundbank", l -> l.getPath().substring(10).equals("Touhou.sf2")).forEach((l, r) -> {
            try {
                soundbank = new SF2Soundbank(r.open());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
