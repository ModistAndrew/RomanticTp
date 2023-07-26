package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.util.FileHelper;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MidiFileLoader implements ResourceManagerReloadListener {
    public HashMap<String, byte[]> resourceMap = new HashMap<>();

    private static final MidiFileLoader instance = new MidiFileLoader();

    public static MidiFileLoader getInstance() {
        return instance;
    }

    public byte[] getMidiData(String name) {
        return resourceMap.get(name) == null ?
                new byte[0] : resourceMap.get(name);
    }

    public byte[] getDefault() {
        return resourceMap.get("default");
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        resourceMap.put("default", new byte[0]); //avoid deletion
        pResourceManager.listResources("midi", l -> l.getPath().endsWith("mid")).forEach((l, r) -> {
            try {
                String path = l.getPath();
                String name = path.substring(5, path.length() - 4); //delete "midi/" and "mid"
                resourceMap.put(name, r.open().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
