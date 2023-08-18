package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = RomanticTp.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), new LanguageGenerator(generator.getPackOutput(), "en_us"));
        generator.addProvider(event.includeClient(), new BlockStateGenerator(generator.getPackOutput(), event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(generator.getPackOutput(), event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new LootTableProvider(generator.getPackOutput(), Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(LootTableGenerator::new, LootContextParamSets.BLOCK))));
        generator.addProvider(event.includeServer(), new RecipeGenerator(generator.getPackOutput()));
    }
}
