package modist.romantictp.data;

import modist.romantictp.common.block.BlockLoader;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class LootTableGenerator extends VanillaBlockLoot {
    @Override
    protected void generate() {
        this.dropSelf(BlockLoader.AUTO_PLAYER.get());
        this.dropSelf(BlockLoader.REVERB_HELMET.get());
        BlockLoader.MUSICIAN_BUSTS.values().forEach(b -> this.dropSelf(b.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> ret = new ArrayList<>();
        ret.add(BlockLoader.AUTO_PLAYER.get());
        ret.add(BlockLoader.REVERB_HELMET.get());
        BlockLoader.MUSICIAN_BUSTS.values().forEach(b -> ret.add(b.get()));
        return ret;
    }
}
