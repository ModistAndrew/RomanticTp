package modist.romantictp.common.entity;

import com.google.common.collect.ImmutableSet;
import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.BlockLoader;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class EntityLoader {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RomanticTp.MODID);
    public static final RegistryObject<EntityType<Melody>> MELODY = ENTITIES.register("melody", () -> EntityType.Builder.of(Melody::new, MobCategory.CREATURE)
            .sized(0.7F, 1.2F)
            .clientTrackingRange(8)
            .updateInterval(2).build("melody"));
}
