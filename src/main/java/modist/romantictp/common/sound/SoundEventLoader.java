package modist.romantictp.common.sound;

import modist.romantictp.RomanticTp;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventLoader {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RomanticTp.MODID);
    @SuppressWarnings("unchecked")
    public static final RegistryObject<SoundEvent> TRUMPET_SOUND = SOUNDS.register("trumpet_sound",
            () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(RomanticTp.MODID, "trumpet_sound"), 1.0F));
}