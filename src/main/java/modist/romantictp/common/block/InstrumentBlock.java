package modist.romantictp.common.block;

import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.item.InstrumentItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class InstrumentBlock extends Block implements EntityBlock {
    public final Instrument defaultInstrument;
    public final List<Instrument> display;

    public InstrumentBlock(Instrument defaultInstrument, List<Instrument> display) {
        super(BlockBehaviour.Properties.of().instabreak().noOcclusion());
        this.defaultInstrument = defaultInstrument;
        this.display = display;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InstrumentBlockEntity(pPos, pState);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.getItem() instanceof InstrumentItem) {
            if (pLevel.getBlockEntity(pPos) instanceof InstrumentBlockEntity be) {
                be.setInstrument(pStack);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.block();
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            if (pLevel.getBlockEntity(pPos) instanceof InstrumentBlockEntity blockEntity) {
                Containers.dropContents(pLevel, pPos, blockEntity.getDrops());
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}
