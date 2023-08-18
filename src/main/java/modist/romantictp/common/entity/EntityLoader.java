package modist.romantictp.common.entity;

import modist.romantictp.RomanticTp;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityLoader {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RomanticTp.MODID);
    public static final RegistryObject<EntityType<Melody>> MELODY = ENTITIES.register("melody", () -> EntityType.Builder.of(Melody::new, MobCategory.CREATURE)
            .sized(0.7F, 1.2F)
            .clientTrackingRange(8)
            .updateInterval(2).build("melody"));
}
