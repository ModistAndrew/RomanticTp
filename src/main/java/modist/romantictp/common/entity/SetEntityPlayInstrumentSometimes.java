package modist.romantictp.common.entity;

import modist.romantictp.RomanticTp;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;
import java.util.function.Predicate;

public class SetEntityPlayInstrumentSometimes {

    public static BehaviorControl<Melody> create(UniformInt pInterval) {
        SetEntityPlayInstrumentSometimes.Ticker setEntityPlayInstrumentSometimes$ticker = new SetEntityPlayInstrumentSometimes.Ticker(pInterval);
        return BehaviorBuilder.create(instance ->
                instance.point((serverLevel, entity, gameTime) -> {
                    if (!setEntityPlayInstrumentSometimes$ticker.tickDownAndCheck(serverLevel.random)) {
                        return false;
                    }
                    if(entity.canPlay() && !entity.isDancing()) {
                        entity.startPlay();
                        return true;
                    }
                    return false;
                }));
    }

    public static final class Ticker {
        private final UniformInt interval;
        private int ticksUntilNextStart;

        public Ticker(UniformInt pInterval) {
            if (pInterval.getMinValue() <= 1) {
                throw new IllegalArgumentException();
            } else {
                this.interval = pInterval;
            }
        }

        public boolean tickDownAndCheck(RandomSource pRandom) {
            if (this.ticksUntilNextStart == 0) {
                this.ticksUntilNextStart = this.interval.sample(pRandom) - 1;
                return false;
            } else {
                return --this.ticksUntilNextStart == 0;
            }
        }
    }
}
