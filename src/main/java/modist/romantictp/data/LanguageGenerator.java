package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import modist.romantictp.client.sound.util.StringHelper;
import modist.romantictp.common.block.BlockLoader;
import modist.romantictp.common.block.MusicianBustBlock;
import modist.romantictp.common.entity.EntityLoader;
import modist.romantictp.common.item.ItemLoader;
import modist.romantictp.util.TooltipProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
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
        add("itemGroup.romantictp_tab", "Romantic Tp");
        add("key.categories.romantictp.instrument", "Romantic Tp instrument keymap");
        for (String s : InstrumentKeyMapping.PITCH_NAMES) {
            add("key.romantictp.pitch." + s, s);
        }
        for (RegistryObject<Block> i : BlockLoader.BLOCKS.getEntries()) {
            if(i.get() instanceof MusicianBustBlock) {
                add(i.get(), StringHelper.title(i.getId().getPath()) + " Bust");
            } else {
                add(i.get(), StringHelper.title(i.getId().getPath()));
            }
        }
        for (RegistryObject<Item> i : ItemLoader.ITEMS.getEntries()) {
            try {
                if(i.get() instanceof BlockItem blockItem && blockItem.getBlock() instanceof MusicianBustBlock) {
                    add(i.get(), StringHelper.title(i.getId().getPath()) + " Bust");
                } else {
                    add(i.get(), StringHelper.title(i.getId().getPath()));
                }
            } catch (IllegalStateException e) {
                RomanticTp.LOGGER.warn("Skipping translation for {}", i.getId().getPath());
            }
        }
        for (RegistryObject<EntityType<?>> i : EntityLoader.ENTITIES.getEntries()) {
            add(i.get(), StringHelper.title(i.getId().getPath()));
        }
        for (String s : StringHelper.TOOLTIPS) {
            add(TooltipProvider.PREFIX + s, StringHelper.title(s));
        }
        add("entity.minecraft.villager.romantictp.musician", "Musician");
    }
}