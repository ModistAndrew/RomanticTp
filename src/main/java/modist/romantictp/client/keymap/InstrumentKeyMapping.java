package modist.romantictp.client.keymap;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class InstrumentKeyMapping {
    private static final String[] pitchNames = new String[]{"C", "D", "E", "F", "G", "A", "B"};
    private static final int[] pitchKeys = new int[]{GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_X,
            GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_B, GLFW.GLFW_KEY_N, GLFW.GLFW_KEY_M};
    private static final int[] intervals = new int[]{0, 2, 4, 5, 7, 9, 11};
    public static final List<Lazy<KeyMapping>> PITCHES = new ArrayList<>();
    static{
        for(int i=0; i<7; i++){
            PITCHES.add(createPitch(pitchNames[i], pitchKeys[i]));
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
        return 60 + intervals[i];
    }
}
