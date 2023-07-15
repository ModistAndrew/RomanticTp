package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class LanguageGenerator extends LanguageProvider {


    public LanguageGenerator(PackOutput output, String locale) {
        super(output, RomanticTp.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("key.romantictp.pitch.D", "D");
        add("key.categories.romantictp.instrument", "instrument");
    }

}