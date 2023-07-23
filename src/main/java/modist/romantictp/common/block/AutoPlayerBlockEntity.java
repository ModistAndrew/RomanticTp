package modist.romantictp.common.block;

import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.item.ScoreItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class AutoPlayerBlockEntity extends BlockEntity {
    private ItemStack score = ItemStack.EMPTY; //count should always be 1
    public boolean isPlaying;

    public AutoPlayerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockLoader.AUTO_PLAYER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void injectScore(ItemStack score) {
        this.score = score.copy();
        this.score.setCount(1);
        setChangedAndUpdate();
    }

    public boolean containsScore() {
        return !score.isEmpty();
    }

    public ItemStack ejectScore() {
        ItemStack oldItemStack = score.copy();
        score = ItemStack.EMPTY;
        setChangedAndUpdate();
        return oldItemStack;
    }

    public String getScoreName() {
        return score.getItem() instanceof ScoreItem scoreItem ?
                scoreItem.getScoreName(score) : "default";
    }

    @Nullable
    public Instrument getInstrument() {
        if(level!=null){
            if(level.getBlockEntity(getBlockPos().above()) instanceof InstrumentBlockEntity be) {
                return be.getInstrument();
            }
        }
        return null;
    }

    public boolean checkPlaying() {
        boolean ret = false;
        if(level!=null) {
            ret = containsScore() && getInstrument() != null && level.hasNeighborSignal(getBlockPos());
        }
        isPlaying=ret;
        return ret;
    }

    public void startSequence() {
        if (level != null && score.getItem() instanceof ScoreItem scoreItem) {
//            InstrumentSoundManager.getInstance().playSequence(InstrumentPlayerManager.getOrCreate(this),
//                    scoreItem.getScoreName(score));
        }
    }

    private void setChangedAndUpdate() {
        this.setChanged();
        if(level!=null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.score = ItemStack.of(compoundTag.getCompound("score"));
        this.isPlaying = compoundTag.getBoolean("isPlaying");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.put("score", this.score.save(new CompoundTag()));
        compoundTag.putBoolean("isPlaying", this.isPlaying);
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
