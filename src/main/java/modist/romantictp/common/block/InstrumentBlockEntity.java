package modist.romantictp.common.block;

import modist.romantictp.common.instrument.Instrument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class InstrumentBlockEntity extends BlockEntity {
    @Nullable
    private Instrument instrument;

    public InstrumentBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockLoader.INSTRUMENT_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
        setChangedAndUpdate();
    }

    public Instrument getInstrument() {
        return this.instrument==null ?
                ((InstrumentBlock)this.getBlockState().getBlock()).defaultInstrument.get() : this.instrument;
    }

    private void setChangedAndUpdate() {
        this.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.instrument = compoundTag.contains("instrument") ? new Instrument(compoundTag.getCompound("instrument")) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (instrument != null) {
            compoundTag.put("instrument", instrument.serializeNBT());
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }
}
