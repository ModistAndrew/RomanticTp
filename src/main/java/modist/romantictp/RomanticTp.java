package modist.romantictp;

import com.mojang.logging.LogUtils;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.SoundEventLoader;
import modist.romantictp.common.block.BlockLoader;
import modist.romantictp.common.entity.EntityLoader;
import modist.romantictp.common.item.ItemLoader;
import modist.romantictp.common.village.VillageLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RomanticTp.MODID)
public class RomanticTp
{
    public static final String MODID = "romantictp";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RomanticTp() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockLoader.BLOCKS.register(modEventBus);
        BlockLoader.BLOCK_ENTITIES.register(modEventBus);
        ItemLoader.ITEMS.register(modEventBus);
        ItemLoader.TABS.register(modEventBus);
        SoundEventLoader.SOUNDS.register(modEventBus);
        VillageLoader.POI_TYPES.register(modEventBus);
        VillageLoader.VILLAGER_PROFESSIONS.register(modEventBus);
        EntityLoader.ENTITIES.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RomanticTpConfig.CONFIG_SPEC);
    }
}
