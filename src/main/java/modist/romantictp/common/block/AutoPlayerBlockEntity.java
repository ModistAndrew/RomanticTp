package modist.romantictp.common.block;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.instrument.ScoreTicker;
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
    //TODO: get message from sequencer for animation
    private ItemStack score = ItemStack.EMPTY; //count should always be 1
    //TODO: manage instrument?
    public Instrument instrument = Instrument.EMPTY; //updated from server
    public boolean powered; //whether is powered
    private boolean isPlaying; //updated from server

    public AutoPlayerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockLoader.AUTO_PLAYER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void injectScore(ItemStack score) {
        this.score = score.copy();
        this.score.setCount(1);
        updateStatus();
    }

    public boolean containsScore() {
        return !score.isEmpty();
    }

    public ItemStack ejectScore() {
        ItemStack oldItemStack = score.copy();
        this.score = ItemStack.EMPTY;
        updateStatus();
        return oldItemStack;
    }

    public Instrument getInstrument() {
        return this.instrument;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void updateStatus() { //server. update status and synchronize data to client
        if(this.level != null && !this.level.isClientSide) {
            this.instrument = detectInstrument();
            updatePlaying(level.hasNeighborSignal(getBlockPos()), containsScore() && !this.instrument.isEmpty() && level.hasNeighborSignal(getBlockPos()));
            setChangedAndUpdate();
        }
    }

    @Nullable
    public Instrument detectInstrument() { //server
        if (level != null) {
            if (level.getBlockEntity(getBlockPos().above()) instanceof InstrumentBlockEntity be) {
                return be.getInstrument();
            }
        }
        return Instrument.EMPTY;
    }

    private void updatePlaying(boolean isPoweredNow, boolean isPlayingNow) { //server.
        if (!this.powered && isPoweredNow && !this.isPlaying && isPlayingNow) {
            this.getCapability(ScoreTicker.SCORE_TICKER).ifPresent(scoreTicker -> scoreTicker.start(
                    score.getItem() instanceof ScoreItem scoreItem ? scoreItem.getTime(score) * 20 : 0L
            ));
        } else if(this.isPlaying && !isPlayingNow) {
            this.getCapability(ScoreTicker.SCORE_TICKER).ifPresent(ScoreTicker::stop);
        }
        this.powered = isPoweredNow;
        this.isPlaying = isPlayingNow;
    }

    private void setChangedAndUpdate() { //server
        this.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void startSequence() { //client
        if (score.getItem() instanceof ScoreItem scoreItem) {
            InstrumentSoundManager.getInstance().startSequence(InstrumentPlayerManager.getOrCreate(this),
                    scoreItem.getMidiData(score), false);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.score = ItemStack.of(compoundTag.getCompound("score")); //score ticker has been loaded
        this.instrument = new Instrument(compoundTag.getCompound("instrument"));
        this.powered = compoundTag.getBoolean("powered");
        this.isPlaying = compoundTag.getBoolean("isPlaying");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.put("score", this.score.save(new CompoundTag()));
        compoundTag.put("instrument", instrument.serializeNBT());
        compoundTag.putBoolean("powered", this.powered);
        compoundTag.putBoolean("isPlaying", this.isPlaying);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) { //called when data from server is updated
        boolean previousPlaying = this.isPlaying;
        boolean previousPowered = this.powered;
        handleUpdateTag(pkt.getTag());
        if (this.powered && !previousPowered && this.isPlaying && !previousPlaying) { //should be triggered only when powered
            startSequence(); //stop will be handled by tick in instance
        }
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

    public void tick() {
        this.getCapability(ScoreTicker.SCORE_TICKER).ifPresent(scoreTicker -> {
            scoreTicker.tick();
            RomanticTp.LOGGER.info(String.valueOf(scoreTicker.getTick()));
            if (!scoreTicker.isPlaying()) {
                this.isPlaying = false;
                setChangedAndUpdate();
            }
        });
    }
}
