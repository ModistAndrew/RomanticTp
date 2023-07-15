package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RomanticTp.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), new SoundDefinitionsGenerator(generator.getPackOutput(), event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new LanguageGenerator(generator.getPackOutput(), "en_us"));
    }
}
