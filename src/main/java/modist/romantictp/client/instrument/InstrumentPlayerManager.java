package modist.romantictp.client.instrument;

import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.item.InstrumentItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InstrumentPlayerManager {
    private static final Map<LivingEntity, InstrumentPlayer> playerMap = new HashMap<>();

    public static InstrumentPlayer getOrCreate(LivingEntity entity) {
        if (!playerMap.containsKey(entity)) {
            playerMap.put(entity, new Player(entity));
        }
        return playerMap.get(entity);
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
                    instrumentItem.getInstrument(entity.getMainHandItem()) : null;
        }

        @Nullable
        @Override
        public Instrument getActiveInstrument() {
            return entity.getUseItem().getItem() instanceof InstrumentItem instrumentItem ?
                    instrumentItem.getInstrument(entity.getUseItem()) : null;
        }
    }
}
