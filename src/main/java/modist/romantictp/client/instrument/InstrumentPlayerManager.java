package modist.romantictp.client.instrument;

import modist.romantictp.common.block.AutoPlayerBlockEntity;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.ScoreItem;
import modist.romantictp.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InstrumentPlayerManager {
    private static final Map<LivingEntity, InstrumentPlayer> entityMap = new HashMap<>();
    private static final Map<AutoPlayerBlockEntity, InstrumentPlayer> blockEntityMap = new HashMap<>();

    public static InstrumentPlayer getOrCreate(LivingEntity entity) {
        if (!entityMap.containsKey(entity)) {
            entityMap.put(entity, new Player(entity));
        }
        return entityMap.get(entity);
    }

    public static InstrumentPlayer getOrCreate(AutoPlayerBlockEntity blockEntity) {
        if (!blockEntityMap.containsKey(blockEntity)) {
            blockEntityMap.put(blockEntity, new BlockPlayer(blockEntity));
        }
        return blockEntityMap.get(blockEntity);
    }

    @Nullable
    public static InstrumentPlayer fromNbt(CompoundTag tag) {
        if(tag.contains("id")){
            Entity entity = Minecraft.getInstance().level.getEntity(tag.getInt("id"));
            return entity instanceof LivingEntity livingEntity ? getOrCreate(livingEntity) : null;
        } else {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(NbtUtils.readBlockPos(tag));
            return blockEntity instanceof AutoPlayerBlockEntity autoPlayerBlockEntity ? getOrCreate(autoPlayerBlockEntity) : null;
        }
    }

    private record Player(LivingEntity entity) implements InstrumentPlayer {
        @Override
        public Vec3 getPos() {
            return entity.position();
        }

        @Override
        public float getVolume() {
            return (90F - entity.getXRot()) / 180F;
        }

        @Override
        public Instrument getInstrument() {
            return entity.getMainHandItem().getItem() instanceof InstrumentItem instrumentItem ?
                    instrumentItem.getInstrument(entity.getMainHandItem()) : Instrument.EMPTY;
        }

        @Override
        public boolean isPlaying() {
            return !getInstrument().isEmpty() && entity.getOffhandItem().getItem() instanceof ScoreItem
                    && entity.getUseItem().getItem() instanceof InstrumentItem;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag ret = new CompoundTag();
            ret.putInt("id", entity.getId());
            return ret;
        }

        @Override
        public boolean isRemoved() {
            return entity.isRemoved();
        }
    }

    private record BlockPlayer(AutoPlayerBlockEntity blockEntity) implements InstrumentPlayer {
        @Override
        public Vec3 getPos() {
            return blockEntity.getBlockPos().getCenter();
        }

        @Override
        public float getVolume() {
            return 1F;
        }

        @Override
        public Instrument getInstrument() {
            return blockEntity.getInstrument();
        }

        @Override
        public boolean isPlaying() {
            return blockEntity.isPlaying();
        }

        @Override
        public CompoundTag serializeNBT() {
            return NbtUtils.writeBlockPos(blockEntity.getBlockPos());
        }

        @Override
        public boolean isRemoved() {
            return blockEntity.isRemoved();
        }
    }
}
