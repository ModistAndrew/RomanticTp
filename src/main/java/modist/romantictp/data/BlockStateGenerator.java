package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.BlockLoader;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateGenerator extends BlockStateProvider {


    public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, RomanticTp.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(BlockLoader.AUTO_PLAYER.get(),
                this.models().getExistingFile(modLoc("auto_player")));
        simpleBlockItem(BlockLoader.AUTO_PLAYER.get(),
                this.models().getExistingFile(modLoc("auto_player")));
        BlockLoader.INSTRUMENTS.forEach((s, b) -> {
            simpleBlock(b.get(), this.models().getExistingFile(modLoc("instrument")));
        });
    }
}
