package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.item.ItemLoader;
import net.minecraft.data.DataGenerator;
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
        withExistingParent(ItemLoader.AUTO_PLAYER.getId().getPath(), modLoc("block/"+ItemLoader.AUTO_PLAYER.getId().getPath()));
    }
}