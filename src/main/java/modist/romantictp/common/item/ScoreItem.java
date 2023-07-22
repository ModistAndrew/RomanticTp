package modist.romantictp.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ScoreItem extends Item {
    public ScoreItem() {
        super(new Item.Properties());
    }

    @Nullable
    public String getScoreName(ItemStack stack) {
        return stack.getTagElement("score") == null ? null : stack.getTagElement("score").getString("name");
    }
}