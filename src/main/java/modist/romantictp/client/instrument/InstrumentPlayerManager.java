package modist.romantictp.client.instrument;

import modist.romantictp.client.sound.util.ClientHelper;
import modist.romantictp.common.block.AutoPlayerBlockEntity;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.ScoreItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

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

    public static void remove(InstrumentPlayer player) {
        entityMap.values().remove(player);
        blockEntityMap.values().remove(player);
    }

    private static double getVelocity(int note) {
        return note < 20 ? 0D : note > 84 ? 1D : (note-20) / 64D;
    }

    private record Player(LivingEntity entity) implements InstrumentPlayer {
        private boolean isPlayer() {
            return entity instanceof net.minecraft.world.entity.player.Player;
        }
        @Override
        public Vec3 getPos() {
            return entity.getEyePosition().add(entity.getForward().scale(0.5D))
                    .add(0, -entity.getBoundingBox().getYsize() / 2D, 0);
        }

        @Override
        public float getVolume() {
            return isPlayer() ? (90F-entity.getXRot()) / 180F : 1F;
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
        public boolean isRemoved() {
            return (!isPlayer()) &&
                    (entity.isRemoved() || !isPlaying() || !ClientHelper.nearToLocalPlayer(getPos()));
        }

        @Override
        public void addParticle(int note) {
            entity.level().addParticle
                    (ParticleTypes.NOTE, getPos().x, getPos().y + 0.7D, getPos().z,
                            getVelocity(note), 0.0D, 0.0D);
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
        public boolean isRemoved() {
            return blockEntity.isRemoved() || !isPlaying() || !ClientHelper.nearToLocalPlayer(getPos());
        }

        @Override
        public void addParticle(int note) {
            blockEntity.getLevel().addParticle
                    (ParticleTypes.NOTE, getPos().x, getPos().y + 0.7D, getPos().z,
                            getVelocity(note), 0.0D, 0.0D);
        }
    }
}
