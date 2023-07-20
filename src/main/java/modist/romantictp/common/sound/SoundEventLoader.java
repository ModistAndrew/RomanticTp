package modist.romantictp.common.sound;

import modist.romantictp.RomanticTp;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class SoundEventLoader {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RomanticTp.MODID);
    public static final Map<String, RegistryObject<SoundEvent>> SOUND_MAP = new HashMap<>();
    static{
        register("trumpet");
    }

    private static void register(String name){
        SOUND_MAP.put(name, SOUNDS.register(name + "_sound",
                () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(RomanticTp.MODID, name + "_sound"), 1.0F)));
    }

    public static RegistryObject<SoundEvent> get(String name){
        return SOUND_MAP.get(name);
    }
}