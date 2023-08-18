package modist.romantictp.common.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class MelodyAi {
    protected static Brain<?> makeBrain(Brain<Melody> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
        return pBrain;
    }

    private static void initCoreActivity(Brain<Melody> pBrain) {
        pBrain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim(0.8F),
                new AnimalPanic(2.5F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)));
    }

    @SuppressWarnings("deprecation")
    private static void initIdleActivity(Brain<Melody> pBrain) {
        pBrain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
                Pair.of(0, GoToWantedItem.create((p_218428_) -> true, 1.75F, true, 32)),
                Pair.of(1, SetEntityPlayInstrumentSometimes.create(UniformInt.of(60, 200))),
                Pair.of(2, StayCloseToTarget.create(MelodyAi::getItemDepositPosition, Predicate.not(MelodyAi::hasWantedItem), 4, 16, 2.25F)),
                Pair.of(3, FlyAroundTarget.create(MelodyAi::getItemDepositPosition)),
                Pair.of(4, SetEntityLookTargetSometimes.create(6.0F, UniformInt.of(30, 60))),
                Pair.of(5, new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.fly(1.0F), 2),
                        Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 2),
                        Pair.of(new DoNothing(30, 60), 1))))), ImmutableSet.of());
    }

    public static void updateActivity(Melody pMelody) {
        pMelody.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    private static Optional<PositionTracker> getItemDepositPosition(LivingEntity p_218424_) {
        Brain<?> brain = p_218424_.getBrain();
        Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        if (optional.isPresent()) {
            GlobalPos globalpos = optional.get();
            if (shouldDepositItemsAtLikedNoteBlock(p_218424_, brain, globalpos)) {
                return Optional.of(new BlockPosTracker(globalpos.pos().above()));
            }

            brain.eraseMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        }

        return getLikedPlayerPositionTracker(p_218424_);
    }

    private static boolean hasWantedItem(LivingEntity p_273346_) {
        Brain<?> brain = p_273346_.getBrain();
        return brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
    }

    private static boolean shouldDepositItemsAtLikedNoteBlock(LivingEntity pEntity, Brain<?> pBrain, GlobalPos pPos) {
        Optional<Integer> optional = pBrain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS);
        Level level = pEntity.level();
        return level.dimension() == pPos.dimension() && level.getBlockState(pPos.pos()).is(Blocks.NOTE_BLOCK) && optional.isPresent();
    }

    private static Optional<PositionTracker> getLikedPlayerPositionTracker(LivingEntity pEntity) {
        return getLikedPlayer(pEntity).map((p_218409_) -> new EntityTracker(p_218409_, true));
    }

    public static Optional<ServerPlayer> getLikedPlayer(LivingEntity pEntity) {
        Level level = pEntity.level();
        if (!level.isClientSide() && level instanceof ServerLevel serverlevel) {
            Optional<UUID> optional = pEntity.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
            if (optional.isPresent()) {
                Entity entity = serverlevel.getEntity(optional.get());
                if (entity instanceof ServerPlayer serverplayer) {
                    if ((serverplayer.gameMode.isSurvival() || serverplayer.gameMode.isCreative()) && serverplayer.closerThan(pEntity, 64.0D)) {
                        return Optional.of(serverplayer);
                    }
                }
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
