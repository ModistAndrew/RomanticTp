package modist.romantictp.common.item;

import modist.romantictp.common.instrument.Instrument;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ScoreItem extends Item {
    public ScoreItem() {
        super(new Item.Properties());
    }

    @Nullable
    public String getScoreName(ItemStack stack) {
        return stack.getTagElement("score") == null ? "default" : stack.getTagElement("score").getString("name");
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
        pTooltip.add(Component.literal(getScoreName(pStack)));
    }
}