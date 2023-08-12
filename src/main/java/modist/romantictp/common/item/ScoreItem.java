package modist.romantictp.common.item;

import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.client.sound.util.MidiHelper;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.swing.text.html.parser.TagElement;
import java.util.ArrayList;
import java.util.List;

public class ScoreItem extends Item {
    public ScoreItem() {
        super(new Item.Properties().stacksTo(1));
    }

    public byte[] getMidiData(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("midiData");
        if (tag == null) {
            return new byte[0];
        }
        return tag.getByteArray("data");
    }

    public long getTime(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("midiData");
        if (tag == null) {
            return 0L;
        }
        return tag.getLong("time");
    }

    public void fillMidiData(ItemStack stack, byte[] data, long time) {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("data", data);
        tag.putLong("time", time);
        stack.addTagElement("midiData", tag);
    }

    public List<ItemStack> getDisplay() {
        List<ItemStack> list = new ArrayList<>();
        MidiFileLoader.getInstance().resourceMap.forEach((s, d) -> {
            if (!s.isEmpty()) {
                ItemStack stack = new ItemStack(this).setHoverName(Component.literal(s));
                fillMidiData(stack, d, MidiHelper.getTime(d));
                list.add(stack);
            }
        });
        return list;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        pTooltip.add(Component.literal(String.valueOf((getTime(pStack)))));
    }
}