package modist.romantictp.common.block;

import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.ScoreItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class AutoPlayerBlock extends Block implements EntityBlock {

    public AutoPlayerBlock() {
        super(BlockBehaviour.Properties.of().instabreak().noOcclusion());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AutoPlayerBlockEntity(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.getBlockEntity(blockPos) instanceof AutoPlayerBlockEntity blockEntity) {
            if (blockEntity.containsScore() && itemStack.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, blockEntity.ejectScore());
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!blockEntity.containsScore() && itemStack.getItem() instanceof ScoreItem) {
                blockEntity.injectScore(itemStack);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.use(blockState, level, blockPos, player, hand, hitResult);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if(pLevel.getBlockEntity(pPos) instanceof AutoPlayerBlockEntity blockEntity) {
            blockEntity.updateStatus();
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return pLevel.getBlockEntity(pPos) instanceof AutoPlayerBlockEntity blockEntity &&
                blockEntity.isPlaying() ? 15 : 0;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (l, s, t, be) -> {
            if (be instanceof AutoPlayerBlockEntity blockEntity && be.getType() == pBlockEntityType) {
                blockEntity.tick();
            }
        };
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(pLevel.getBlockState(pPos.above()).getBlock() instanceof InstrumentBlock) {
            pLevel.destroyBlock(pPos.above(), true);
        }
        if (!pState.is(pNewState.getBlock())) {
            if (pLevel.getBlockEntity(pPos) instanceof AutoPlayerBlockEntity blockEntity) {
                Containers.dropContents(pLevel, pPos, blockEntity.getDrops());
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}
