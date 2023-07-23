package modist.romantictp.common.block;

import modist.romantictp.common.item.ScoreItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
            if (blockEntity.containsScore()) {
                ItemHandlerHelper.giveItemToPlayer(player, blockEntity.ejectScore());
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (itemStack.getItem() instanceof ScoreItem) {
                blockEntity.injectScore(itemStack);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.use(blockState, level, blockPos, player, hand, hitResult);
    }
}
