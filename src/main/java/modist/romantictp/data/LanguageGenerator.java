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
        add("key.romantictp.pitch.C", "C");
        add("key.romantictp.pitch.D", "D");
        add("key.romantictp.pitch.E", "E");
        add("key.romantictp.pitch.F", "F");
        add("key.romantictp.pitch.G", "G");
        add("key.romantictp.pitch.A", "A");
        add("key.romantictp.pitch.B", "B");
        add("key.categories.romantictp.instrument", "instrument");
        add("itemGroup.romantictp_tab", "romantictp");
    }
}