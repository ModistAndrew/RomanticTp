package modist.romantictp.common.block;

import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.item.InstrumentItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class InstrumentBlock extends Block implements EntityBlock {
    public final Supplier<Instrument> defaultInstrument;

    public InstrumentBlock(Supplier<Instrument> defaultInstrument) {
        super(BlockBehaviour.Properties.of());
        this.defaultInstrument = defaultInstrument;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InstrumentBlockEntity(pPos, pState);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.getItem() instanceof InstrumentItem item) {
            Instrument instrument = item.getInstrument(pStack);
            if (pLevel.getBlockEntity(pPos) instanceof InstrumentBlockEntity be) {
                be.setInstrument(instrument);
            }
        }
    }

}
