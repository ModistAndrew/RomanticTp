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
import java.util.List;

public class ScoreItem extends Item {
    public ScoreItem() {
        super(new Item.Properties().stacksTo(1));
    }

    public byte[] getMidiData(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("midiData");
        if(tag == null){
            return MidiFileLoader.getInstance().getDefault();
        }
        return tag.getByteArray("data");
    }

    public byte[] fillMidiData(ItemStack stack) { //client
        return fillMidiData(stack, MidiFileLoader.getInstance().getMidiData(stack.getHoverName().getString()));
    }

    public byte[] fillMidiData(ItemStack stack, byte[] data) { //server
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("data", data);
        stack.addTagElement("midiData", tag);
        return data;
    }

    public List<ItemStack> getDisplay() {
        ItemStack stack = new ItemStack(this);
        CompoundTag tag = new CompoundTag();
        tag.putString("name", "test");
        stack.addTagElement("score", tag);
        return List.of(stack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        pTooltip.add(Component.literal(String.valueOf((getMidiData(pStack).length))));
    }
}