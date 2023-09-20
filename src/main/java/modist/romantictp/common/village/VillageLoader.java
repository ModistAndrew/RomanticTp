package modist.romantictp.common.village;

import com.google.common.collect.ImmutableSet;
import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.BlockLoader;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class VillageLoader {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, RomanticTp.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, RomanticTp.MODID);
    public static final RegistryObject<PoiType> MUSICIAN_POI = POI_TYPES.register("musician_poi", () ->
            new PoiType(Set.of(BlockLoader.AUTO_PLAYER.get().defaultBlockState()), 1, 1));
    public static final RegistryObject<VillagerProfession> MUSICIAN = VILLAGER_PROFESSIONS.register("musician", () ->
            new VillagerProfession("musician", poiTypeHolder -> poiTypeHolder.get() == MUSICIAN_POI.get(),
                    poiTypeHolder -> poiTypeHolder.get() == MUSICIAN_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_FISHERMAN)); //TODO: add our own sound
}
