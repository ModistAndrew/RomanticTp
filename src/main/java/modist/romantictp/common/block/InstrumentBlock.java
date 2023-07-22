package modist.romantictp.common.block;

import modist.romantictp.common.instrument.Instrument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class InstrumentBlock extends Block implements EntityBlock {
    public final Supplier<Instrument> defaultInstrument;

    public InstrumentBlock(Supplier<Instrument> defaultInstrument) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW));
        this.defaultInstrument = defaultInstrument;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InstrumentBlockEntity(pPos, pState);
    }

}
