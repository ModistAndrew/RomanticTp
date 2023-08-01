package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.BlockLoader;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LootTableGenerator extends VanillaBlockLoot {
    @Override
    protected void generate() {
        this.dropSelf(BlockLoader.AUTO_PLAYER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(BlockLoader.AUTO_PLAYER.get());
    }
}
