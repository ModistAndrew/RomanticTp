package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.item.ItemLoader;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RomanticTp.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTexture(ItemLoader.SCORE.getId().getPath(), mcLoc("item/generated"), "layer0",
                modLoc("item/"+ItemLoader.SCORE.getId().getPath()));
        ItemLoader.INSTRUMENTS.forEach((s, i) ->
                singleTexture(s, mcLoc("item/generated"), "layer0",
                        s.startsWith("default_instrument_") ? modLoc("item/default_instrument") : modLoc("item/"+s)));
        singleTexture(ItemLoader.NATURAL_TRUMPET.getId().getPath(), mcLoc("item/generated"), "layer0",
                modLoc("item/"+ItemLoader.NATURAL_TRUMPET.getId().getPath()));
        singleTexture(ItemLoader.SOUTH_SECTION_ONE.getId().getPath(), mcLoc("item/generated"), "layer0",
                modLoc("item/"+ItemLoader.SOUTH_SECTION_ONE.getId().getPath()));
        withExistingParent(ItemLoader.MELODY_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }
}
