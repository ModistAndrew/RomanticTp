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
    Capability<ScoreTicker> SCORE_TICKER = CapabilityManager.get(new CapabilityToken<>() {
    });

    void start(long maxTick);

    void tick();

    long getTick();

    boolean isPlaying();

    class ScoreTickerImpl implements ScoreTicker {
        @Nullable
        private final BlockEntity blockEntity;
        private long leftTick;
        private long maxTick;

        public ScoreTickerImpl(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public void start(long maxTick) {
            this.maxTick = maxTick;
            this.leftTick = maxTick; //lazy
            setChanged();
        }

        public void tick() {
            if (leftTick <= 0) {
                return;
            }
            leftTick--;
            setChanged();
        }

        @Override
        public long getTick() {
            return leftTick;
        }

        public boolean isPlaying() {
            return leftTick > 0;
        }

        private void setChanged() {
            if (blockEntity != null) {
                blockEntity.setChanged();
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag ret = new CompoundTag();
            ret.putLong("leftTick", leftTick);
            ret.putLong("maxTick", maxTick);
            return ret;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            leftTick = nbt.getLong("leftTick");
            maxTick = nbt.getLong("maxTick");
        }
    }

    class ScoreTickerProvider implements ICapabilitySerializable<CompoundTag> {
        private final ScoreTicker backend;
        private final LazyOptional<ScoreTicker> optionalData;

        public ScoreTickerProvider(BlockEntity blockEntity) {
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
