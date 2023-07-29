package modist.romantictp.common.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.ItemLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class Melody extends PathfinderMob implements InventoryCarrier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
    private static final int LIFTING_ITEM_ANIMATION_DURATION = 5;
    private static final float DANCING_LOOP_DURATION = 55.0F;
    private static final float SPINNING_ANIMATION_DURATION = 15.0F;
    private static final Ingredient DUPLICATION_ITEM = Ingredient.of(Items.AMETHYST_SHARD);
    private static final int DUPLICATION_COOLDOWN_TICKS = 6000;
    private static final int NUM_OF_DUPLICATION_HEARTS = 3;
    private static final double RIDING_OFFSET = 0.4D;
    private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(Melody.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CAN_DUPLICATE = SynchedEntityData.defineId(Melody.class, EntityDataSerializers.BOOLEAN);
    protected static final ImmutableList<SensorType<? extends Sensor<? super Melody>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.LIKED_PLAYER, MemoryModuleType.LIKED_NOTEBLOCK_POSITION, MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.IS_PANICKING);
    public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, 2.5F, 3.0F, 3.75F, 4.0F);
    private VibrationSystem.Data vibrationData;
    private final SimpleContainer inventory = new SimpleContainer(1);
    private @org.jetbrains.annotations.Nullable BlockPos jukeboxPos;
    private long duplicationCooldown;
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float dancingAnimationTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;

    public Melody(EntityType<? extends Melody> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
        this.vibrationData = new VibrationSystem.Data();
    }

    @Override
    public void setItemInHand(InteractionHand pHand, ItemStack pStack) {
        super.setItemInHand(pHand, pStack);
        if (pHand == InteractionHand.MAIN_HAND && pStack.getItem() instanceof InstrumentItem) {
            super.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemLoader.SCORE.get()));
            startUsingItem(pHand); //TODO: delay (after instance tick to change instrument)
        }
    }

    protected Brain.Provider<Melody> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return MelodyAi.makeBrain(this.brainProvider().makeBrain(pDynamic));
    }

    public Brain<Melody> getBrain() {
        return (Brain<Melody>)super.getBrain();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FLYING_SPEED, (double)0.1F).add(Attributes.MOVEMENT_SPEED, (double)0.1F).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANCING, false);
        this.entityData.define(DATA_CAN_DUPLICATE, true);
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return pDimensions.height * 0.6F;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        Entity $$3 = pSource.getEntity();
        if ($$3 instanceof Player player) {
            Optional<UUID> optional = this.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
            if (optional.isPresent() && player.getUUID().equals(optional.get())) {
                return false;
            }
        }

        return super.hurt(pSource, pAmount);
    }

    protected void playStepSound(BlockPos pPos, BlockState pState) {
    }

    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    protected SoundEvent getAmbientSound() {
        return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ALLAY_DEATH;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    protected void customServerAiStep() {
        this.level().getProfiler().push("MelodyBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("MelodyActivityUpdate");
        MelodyAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    public void aiStep() {
        super.aiStep();
//        if (!this.level().isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
//            this.heal(1.0F);
//        }
//
//        if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
//            this.setDancing(false);
//            this.jukeboxPos = null;
//        }

        this.updateDuplicationCooldown();
    }

    public void tick() {
        super.tick();
//        if (this.level().isClientSide) {
//            this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
//            if (this.hasItemInHand()) {
//                this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
//            } else {
//                this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
//            }
//
//            if (this.isDancing()) {
//                ++this.dancingAnimationTicks;
//                this.spinningAnimationTicks0 = this.spinningAnimationTicks;
//                if (this.isSpinning()) {
//                    ++this.spinningAnimationTicks;
//                } else {
//                    --this.spinningAnimationTicks;
//                }
//
//                this.spinningAnimationTicks = Mth.clamp(this.spinningAnimationTicks, 0.0F, 15.0F);
//            } else {
//                this.dancingAnimationTicks = 0.0F;
//                this.spinningAnimationTicks = 0.0F;
//                this.spinningAnimationTicks0 = 0.0F;
//            }
//        } else {
//            if (this.isPanicking()) {
//                this.setDancing(false);
//            }
//        }

    }

    public boolean canPickUpLoot() {
        return !this.isOnPickupCooldown() && this.hasItemInHand();
    }

    public boolean hasItemInHand() {
        return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    public boolean canTakeItem(ItemStack pItemstack) {
        return false;
    }

    private boolean isOnPickupCooldown() {
        return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
    }

    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        ItemStack itemstack1 = this.getItemInHand(InteractionHand.MAIN_HAND);
        if (this.isDancing() && this.isDuplicationItem(itemstack) && this.canDuplicate()) {
            this.duplicateMelody();
            this.level().broadcastEntityEvent(this, (byte)18);
            this.level().playSound(pPlayer, this, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.removeInteractionItem(pPlayer, itemstack);
            return InteractionResult.SUCCESS;
        } else if (itemstack1.isEmpty() && !itemstack.isEmpty()) {
            ItemStack itemstack3 = itemstack.copyWithCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, itemstack3);
            this.removeInteractionItem(pPlayer, itemstack);
            this.level().playSound(pPlayer, this, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, pPlayer.getUUID());
            return InteractionResult.SUCCESS;
        } else if (!itemstack1.isEmpty() && pHand == InteractionHand.MAIN_HAND && itemstack.isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.level().playSound(pPlayer, this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.swing(InteractionHand.MAIN_HAND);

            for(ItemStack itemstack2 : this.getInventory().removeAllItems()) {
                BehaviorUtils.throwItem(this, itemstack2, this.position());
            }

            this.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
            pPlayer.addItem(itemstack1);
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    public void setJukeboxPlaying(BlockPos pJukeboxPos, boolean pJukeboxPlaying) {
        if (pJukeboxPlaying) {
            if (!this.isDancing()) {
                this.jukeboxPos = pJukeboxPos;
                this.setDancing(true);
            }
        } else if (pJukeboxPos.equals(this.jukeboxPos) || this.jukeboxPos == null) {
            this.jukeboxPos = null;
            this.setDancing(false);
        }

    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    protected Vec3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    public boolean wantsToPickUp(ItemStack pStack) {
        ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
        return !itemstack.isEmpty() && this.inventory.canAddItem(pStack) && this.MelodyConsidersItemEqual(itemstack, pStack) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this);
    }

    private boolean MelodyConsidersItemEqual(ItemStack pFirst, ItemStack pSecond) {
        return ItemStack.isSameItem(pFirst, pSecond) && !this.hasNonMatchingPotion(pFirst, pSecond);
    }

    private boolean hasNonMatchingPotion(ItemStack pFirst, ItemStack pSecond) {
        CompoundTag compoundtag = pFirst.getTag();
        boolean flag = compoundtag != null && compoundtag.contains("Potion");
        if (!flag) {
            return false;
        } else {
            CompoundTag compoundtag1 = pSecond.getTag();
            boolean flag1 = compoundtag1 != null && compoundtag1.contains("Potion");
            if (!flag1) {
                return true;
            } else {
                Tag tag = compoundtag.get("Potion");
                Tag tag1 = compoundtag1.get("Potion");
                return tag != null && tag1 != null && !tag.equals(tag1);
            }
        }
    }

    protected void pickUpItem(ItemEntity pItemEntity) {
        InventoryCarrier.pickUpItem(this, this, pItemEntity);
    }

    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    public boolean isFlapping() {
        return !this.onGround();
    }

    public boolean isDancing() {
        return this.entityData.get(DATA_DANCING);
    }

    public boolean isPanicking() {
        return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
    }

    public void setDancing(boolean pDancing) {
        if (!this.level().isClientSide && this.isEffectiveAi() && (!pDancing || !this.isPanicking())) {
            this.entityData.set(DATA_DANCING, pDancing);
        }
    }

    private boolean shouldStopDancing() {
        return this.jukeboxPos == null || !this.jukeboxPos.closerToCenterThan(this.position(), (double)GameEvent.JUKEBOX_PLAY.getNotificationRadius()) || !this.level().getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
    }

    public float getHoldingItemAnimationProgress(float pPartialTick) {
        return Mth.lerp(pPartialTick, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
    }

    public boolean isSpinning() {
        float f = this.dancingAnimationTicks % 55.0F;
        return f < 15.0F;
    }

    public float getSpinningProgress(float pPartialTick) {
        return Mth.lerp(pPartialTick, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0F;
    }

    public boolean equipmentHasChanged(ItemStack pOldItem, ItemStack pNewItem) {
        return !this.MelodyConsidersItemEqual(pOldItem, pNewItem);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
            this.spawnAtLocation(itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.writeInventoryToTag(pCompound);
        VibrationSystem.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationData).resultOrPartial(LOGGER::error).ifPresent((p_218353_) -> {
            pCompound.put("listener", p_218353_);
        });
        pCompound.putLong("DuplicationCooldown", this.duplicationCooldown);
        pCompound.putBoolean("CanDuplicate", this.canDuplicate());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readInventoryFromTag(pCompound);
        if (pCompound.contains("listener", 10)) {
            VibrationSystem.Data.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, pCompound.getCompound("listener"))).resultOrPartial(LOGGER::error).ifPresent((p_281082_) -> {
                this.vibrationData = p_281082_;
            });
        }

        this.duplicationCooldown = (long)pCompound.getInt("DuplicationCooldown");
        this.entityData.set(DATA_CAN_DUPLICATE, pCompound.getBoolean("CanDuplicate"));
    }

    protected boolean shouldStayCloseToLeashHolder() {
        return false;
    }

    private void updateDuplicationCooldown() {
        if (this.duplicationCooldown > 0L) {
            --this.duplicationCooldown;
        }

        if (!this.level().isClientSide() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
            this.entityData.set(DATA_CAN_DUPLICATE, true);
        }

    }

    private boolean isDuplicationItem(ItemStack pStack) {
        return DUPLICATION_ITEM.test(pStack);
    }

    private void duplicateMelody() {
        Melody Melody = EntityLoader.MELODY.get().create(this.level());
        if (Melody != null) {
            Melody.moveTo(this.position());
            Melody.setPersistenceRequired();
            Melody.resetDuplicationCooldown();
            this.resetDuplicationCooldown();
            this.level().addFreshEntity(Melody);
        }

    }

    private void resetDuplicationCooldown() {
        this.duplicationCooldown = 6000L;
        this.entityData.set(DATA_CAN_DUPLICATE, false);
    }

    private boolean canDuplicate() {
        return this.entityData.get(DATA_CAN_DUPLICATE);
    }

    private void removeInteractionItem(Player pPlayer, ItemStack pStack) {
        if (!pPlayer.getAbilities().instabuild) {
            pStack.shrink(1);
        }
    }

    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, (double)this.getEyeHeight() * 0.6D, (double)this.getBbWidth() * 0.1D);
    }

    public double getMyRidingOffset() {
        return 0.4D;
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 18) {
            for(int i = 0; i < 3; ++i) {
                this.spawnHeartParticle();
            }
        } else {
            super.handleEntityEvent(pId);
        }

    }

    private void spawnHeartParticle() {
        double d0 = this.random.nextGaussian() * 0.02D;
        double d1 = this.random.nextGaussian() * 0.02D;
        double d2 = this.random.nextGaussian() * 0.02D;
        this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
    }
}
