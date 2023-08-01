package modist.romantictp.common.item;

import modist.romantictp.client.sound.InstrumentSoundManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NaturalTrumpetItem extends Item {
    public NaturalTrumpetItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        pPlayer.startUsingItem(pUsedHand);
        if(pPlayer.level().isClientSide) {
            InstrumentSoundManager.getInstance().playNaturalTrumpet(pPlayer);
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000; //INFINITY
    }
}
