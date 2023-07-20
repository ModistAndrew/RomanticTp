package modist.romantictp.common.item;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class InstrumentItem<T extends Instrument> extends Item {
    private final Supplier<T> randomInstrument;
    private final Function<CompoundTag, T> instrumentFromTag;
    public InstrumentItem(Function<CompoundTag, T> instrumentFromTag,Supplier<T> randomInstrument) {
        super(new Item.Properties().stacksTo(1));
        this.instrumentFromTag = instrumentFromTag;
        this.randomInstrument = randomInstrument;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000; //INFINITY
    }

    public void startPlay(LivingEntity player, ItemStack stack, float pitch, float volume) {
        if(player.level().isClientSide) {
            tryCreateRandomInstrument(stack);
            InstrumentSoundManager.getInstance().startPlay(player, getInstrument(stack), pitch, volume);
        }
    }

    public void stopPlay(LivingEntity player, ItemStack stack) {
        if(player.level().isClientSide) {
            InstrumentSoundManager.getInstance().stopPlay(player, getInstrument(stack));
        }
    }

    @Nullable
    public Instrument getInstrument(ItemStack stack) {
        return stack.getTagElement("instrument") == null ? null : instrumentFromTag.apply(stack.getTagElement("instrument"));
    }

    public void tryCreateRandomInstrument(ItemStack stack) {
        if(stack.getTagElement("instrument") == null) {
            Instrument instrument = randomInstrument.get();
            stack.addTagElement("instrument", instrument.serializeNBT());
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        Instrument instrument = getInstrument(pStack);
        if(instrument == null){
            pTooltip.add(Component.literal("???????"));
            return;
        }
        instrument.addText(pTooltip, pIsAdvanced.isAdvanced());
    }
}
