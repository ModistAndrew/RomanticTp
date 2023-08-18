package modist.romantictp.client.keymap;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;

public class InstrumentKeyMapping {
    public static final String[] PITCH_NAMES = new String[]{"C3", "D3", "E3", "F3", "G3", "A3", "B3",
            "C4", "D4", "E4", "F4", "G4", "A4", "B4", "C5", "D5", "E5", "F5", "G5", "A5", "B5"};
    private static final int[] PITCH_KEYS = new int[]{-1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final int[] INTERVALS = new int[]{-12, -10, -8, -7, -5, -3, -1,
            0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23};
    public static final List<Lazy<KeyMapping>> PITCHES = new ArrayList<>();
    static{
        for(int i=0; i<21; i++){
            PITCHES.add(createPitch(PITCH_NAMES[i], PITCH_KEYS[i]));
        }
    }

    private static Lazy<KeyMapping> createPitch(String name, int key){
        return Lazy.of(() -> new KeyMapping(
                "key.romantictp.pitch." + name,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                key,
                "key.categories.romantictp.instrument"
        ));
    }

    public static int getPitch(int i){
        return 60 + INTERVALS[i];
    }
}
