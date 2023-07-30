package modist.romantictp.common.instrument;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//used on server to manage when the sequence should stop
@AutoRegisterCapability
public interface ScoreTicker extends INBTSerializable<CompoundTag> {
    Capability<ScoreTicker> SCORE_TICKER = CapabilityManager.get(new CapabilityToken<ScoreTicker>() {});

    void start(long maxTick);
    void stop();

    void tick();
    long getTick();

    boolean isPlaying();

    class ScoreTickerImpl implements ScoreTicker {
        @Nullable
        private final BlockEntity blockEntity;
        private long leftTick;
        private long maxTick;
        private boolean isPlaying;

        public ScoreTickerImpl(BlockEntity blockEntity){
            this.blockEntity = blockEntity;
        }

        @Override
        public void start(long maxTick) {
            this.maxTick = maxTick;
            this.leftTick = maxTick; //lazy
            this.isPlaying = true;
            setChanged();
        }

        @Override
        public void stop() {
            this.isPlaying = false;
            setChanged();
        }

        public void tick() {
            if (isPlaying) {
                leftTick--;
                setChanged();
                if(leftTick <= 0) {
                    stop();
                }
            }
        }

        @Override
        public long getTick() {
            return leftTick;
        }

        public boolean isPlaying() {
            return  isPlaying;
        }

        private void setChanged() {
            if(blockEntity!=null){
                blockEntity.setChanged();
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag ret = new CompoundTag();
            ret.putLong("leftTick", leftTick);
            ret.putLong("maxTick", maxTick);
            ret.putBoolean("isPlaying", isPlaying);
            return ret;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            leftTick = nbt.getLong("leftTick");
            maxTick = nbt.getLong("maxTick");
            isPlaying = nbt.getBoolean("isPlaying");
        }
    }

    class ScoreTickerProvider implements ICapabilitySerializable<CompoundTag> {
        @Nullable
        private final BlockEntity blockEntity;
        private final ScoreTicker backend;
        private final LazyOptional<ScoreTicker> optionalData;

        public ScoreTickerProvider(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
            this.backend = new ScoreTicker.ScoreTickerImpl(blockEntity);
            this.optionalData = LazyOptional.of(() -> backend);
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return SCORE_TICKER.orEmpty(cap, this.optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }
}
