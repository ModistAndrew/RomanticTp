package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.util.StringHelper;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MidiFileLoader implements ResourceManagerReloadListener {
    public Map<String, byte[]> resourceMap = new TreeMap<>();

    private static final MidiFileLoader instance = new MidiFileLoader();

    public static MidiFileLoader getInstance() {
        return instance;
    }

    public byte[] getMidiData(String name) {
        return resourceMap.get(name) == null ?
                new byte[0] : resourceMap.get(name);
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        pResourceManager.listResources("midi", l -> l.getPath().endsWith("mid")).forEach((l, r) -> {
            try {
                String path = l.getPath();
                String name = path.substring(5, path.length() - 4); //delete "midi/" and "mid"
                if(StringHelper.validMidiName(name)) {
                    resourceMap.put(name, r.open().readAllBytes());
                } else {
                    RomanticTp.LOGGER.warn("Midi file named {} should contain at most 2 - to split name, author and section. Skipping.", name);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
