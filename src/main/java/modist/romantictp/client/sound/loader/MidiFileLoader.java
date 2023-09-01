package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.util.MidiInfo;
import modist.romantictp.util.StringHelper;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MidiFileLoader implements ResourceManagerReloadListener {
    public LinkedHashMap<String, MidiInfo> resourceMap = new LinkedHashMap<>();
    private static final MidiFileLoader instance = new MidiFileLoader();

    public static MidiFileLoader getInstance() {
        return instance;
    }

    public MidiInfo getMidiInfo(String name) {
        return resourceMap.getOrDefault(name, MidiInfo.EMPTY);
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        resourceMap.clear();
        pResourceManager.listResources("midi", l -> l.getPath().endsWith("mid")).forEach((l, r) -> {
            try {
                String fullPath = l.getPath();
                String path = fullPath.substring(5, fullPath.length() - 4); //delete "midi/" and "mid"
                String name = StringHelper.getDisplayName(path);
                if (StringHelper.validMidiName(path)) {
                    MidiInfo info = MidiInfo.create(path, r.open().readAllBytes());
                    if (resourceMap.put(name, info) == null) {
                        RomanticTp.LOGGER.info("Midi file named {} loaded", name);
                    } else {
                        RomanticTp.LOGGER.warn("Midi file named {} is duplicate", name);
                    }
                } else {
                    RomanticTp.LOGGER.warn("Midi file named {} should contain at most 2 -- to split name, author and section. Skipping.", name);
                }
            } catch (IOException e) {
                RomanticTp.LOGGER.error("Fail to load midi file", e);
            }
        });
        sortByValue();
    }

    private void sortByValue() {
        List<Map.Entry<String, MidiInfo>> list = new LinkedList<>(resourceMap.entrySet());
        list.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()));
        resourceMap = list.stream().collect
                (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> b, LinkedHashMap::new));
    }
}
