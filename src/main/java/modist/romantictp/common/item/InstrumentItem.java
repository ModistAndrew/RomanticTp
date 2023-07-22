package modist.romantictp.common.item;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class InstrumentItem<T extends Instrument> extends Item { //TODO: one class is OK
    private final Supplier<T> randomInstrument;
    private final Function<CompoundTag, T> instrumentFromTag;

    public InstrumentItem(Function<CompoundTag, T> instrumentFromTag, Supplier<T> randomInstrument) {
        super(new Item.Properties().stacksTo(1));
        this.instrumentFromTag = instrumentFromTag;
        this.randomInstrument = randomInstrument;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000; //INFINITY
    }

    public void startPlay(LivingEntity player, int pitch, int volume) {
        RomanticTp.info("start play" + System.currentTimeMillis());
        tryCreateRandomInstrument(player.getMainHandItem());
        if (player.level().isClientSide) {
            InstrumentSoundManager.getInstance().startPlay(InstrumentPlayerManager.getOrCreate(player), pitch, volume);
        }
    }

    public void stopPlay(LivingEntity player, int pitch, int volume) {
        tryCreateRandomInstrument(player.getMainHandItem());
        if (player.level().isClientSide) {
            InstrumentSoundManager.getInstance().stopPlay(InstrumentPlayerManager.getOrCreate(player), pitch, volume);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.MAIN_HAND) {
            tryCreateRandomInstrument(pPlayer.getMainHandItem());
            pPlayer.startUsingItem(pUsedHand);
            if (pPlayer.level().isClientSide) {
                InstrumentSoundManager.getInstance().playSequence(InstrumentPlayerManager.getOrCreate(pPlayer), "test");
            }
            return InteractionResultHolder.consume(pPlayer.getMainHandItem());
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Nullable
    public Instrument getInstrument(ItemStack stack) {
        return stack.getTagElement("instrument") == null ? null : instrumentFromTag.apply(stack.getTagElement("instrument"));
    }

    public void tryCreateRandomInstrument(ItemStack stack) { //should be called before any call to play sound, in order to avoid np
        if (stack.getTagElement("instrument") == null) {
            Instrument instrument = randomInstrument.get();
            stack.addTagElement("instrument", instrument.serializeNBT());
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        Instrument instrument = getInstrument(pStack);
        if (instrument == null) {
            pTooltip.add(Component.literal("???????"));
            return;
        }
        instrument.addText(pTooltip, pIsAdvanced.isAdvanced());
    }
}
