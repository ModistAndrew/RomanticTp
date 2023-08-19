package modist.romantictp.common.item;

import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.util.ItemDisplayProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScoreItem extends Item implements ItemDisplayProvider { //TODO: show score with color
    public ScoreItem() {
        super(new Item.Properties().stacksTo(16));
    }

    public MidiHelper.MidiInfo getMidiInfo(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("midiData");
        return tag == null ? MidiHelper.MidiInfo.EMPTY : new MidiHelper.MidiInfo(tag);
    }

    public byte[] getMidiData(ItemStack stack) {
        return getMidiInfo(stack).data();
    }

    public long getTime(ItemStack stack) {
        return getMidiInfo(stack).time();
    }

    public MidiHelper.MidiInfo setMidiData(ItemStack stack, String name) { //client only
        MidiHelper.MidiInfo info = MidiHelper.getInfo(name);
        fillMidiData(stack, info);
        return info;
    }

    public void fillMidiData(ItemStack stack, MidiHelper.MidiInfo info) { //server only
        stack.addTagElement("midiData", info.serializeNBT());
    }

    @Override
    public List<ItemStack> getDisplay() { //client only
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(this));
        MidiFileLoader.getInstance().resourceMap.keySet().forEach(s -> {
            if (!s.isEmpty()) {
                ItemStack stack = new ItemStack(this);
                setMidiData(stack, s);
                list.add(stack);
            }
        });
        return list;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        getMidiInfo(pStack).addText(pTooltip, pIsAdvanced.isAdvanced());
    }
}