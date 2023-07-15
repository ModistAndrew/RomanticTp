package modist.romantictp.common.item;

import modist.romantictp.client.sound.TrumpetSoundInstance;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TrumpetItem extends Item {
    private Map<ItemStack, TrumpetSoundInstance> soundInstanceCache = new HashMap<>();
    public TrumpetItem() {
        super(new Item.Properties());
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000; //INFINITY
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if(pLevel.isClientSide) {
            TrumpetSoundInstance soundInstance = new TrumpetSoundInstance(pPlayer, itemstack, SoundEventLoader.TRUMPET_SOUND.get(), SoundSource.PLAYERS);
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            soundInstanceCache.put(itemstack, soundInstance);
        }
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }
}
