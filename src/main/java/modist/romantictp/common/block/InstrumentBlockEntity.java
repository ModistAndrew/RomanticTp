package modist.romantictp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class InstrumentBlockEntity extends BlockEntity {
    public InstrumentBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockLoader.INSTRUMENT_BLOCK_ENTITY.get(), pPos, pBlockState);
    }
}
