package modist.romantictp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class InstrumentBlock extends Block implements EntityBlock {
    public InstrumentBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InstrumentBlockEntity(pPos, pState);
    }


}
