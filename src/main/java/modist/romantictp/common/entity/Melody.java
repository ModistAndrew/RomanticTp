package modist.romantictp.common.entity;

import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.ItemLoader;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class Melody extends Allay {
    public Melody(EntityType<? extends Allay> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void setItemInHand(InteractionHand pHand, ItemStack pStack) {
        super.setItemInHand(pHand, pStack);
        if(pHand == InteractionHand.MAIN_HAND && pStack.getItem() instanceof InstrumentItem) {
            startUsingItem(pHand);
            super.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemLoader.SCORE.get()));
        }
    }
}
