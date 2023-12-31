package modist.romantictp.client.sound;

import modist.romantictp.RomanticTp;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventLoader {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RomanticTp.MODID);

    public static final RegistryObject<SoundEvent> BLANK = SOUNDS.register("blank", () -> SoundEvent.createFixedRangeEvent(
            new ResourceLocation(RomanticTp.MODID, "blank"), 1.0F));
    public static final RegistryObject<SoundEvent> NATURAL_TRUMPET = SOUNDS.register("natural_trumpet",
            () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(RomanticTp.MODID, "natural_trumpet"), 1.0F));
}