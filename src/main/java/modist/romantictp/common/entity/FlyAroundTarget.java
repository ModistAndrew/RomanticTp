package modist.romantictp.common.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class FlyAroundTarget {
    private static final Random RANDOM = new Random();
    public static BehaviorControl<Melody> create(Function<LivingEntity, Optional<PositionTracker>> pTargetPositionGetter) {
        return BehaviorBuilder.create(instance -> instance.group
                        (instance.present(MemoryModuleType.LIKED_PLAYER), instance.absent(MemoryModuleType.WALK_TARGET))
                .apply(instance, (uuidMemoryAccessor, walkTargetMemoryAccessor) -> (serverLevel, entity, gameTime) -> {
                    Optional<PositionTracker> optional = pTargetPositionGetter.apply(entity);
                    if (optional.isPresent() && entity.isPlaying()) {
                        Vec3 v = optional.get().currentPosition().subtract(entity.position());
                        v = new Vec3(v.x, 0, v.z);
                        walkTargetMemoryAccessor.set(new WalkTarget(
                                optional.get().currentPosition().add(v.yRot(RANDOM.nextFloat(0F, 1.5F)).add(0, RANDOM.nextDouble(-1D, 1D), 0)),
                                2F, 0));
                        return true;
                    }
                    return false;
                }));
    }
}
