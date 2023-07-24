package modist.romantictp.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import modist.romantictp.client.sound.audio.EFXManager;
import modist.romantictp.common.item.InstrumentItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void changeSound(PlaySoundSourceEvent event) {
        EFXManager.getInstance().applyEFX(event.getChannel().source);
    }

    @SubscribeEvent
    public static void startPlay(InputEvent.Key event) {
        for (int i = 0; i < 7; i++) {
            Lazy<KeyMapping> k = InstrumentKeyMapping.PITCHES.get(i);
            if (event.getKey() == k.get().getKey().getValue()) {
                LocalPlayer player = Minecraft.getInstance().player;
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (stack.getItem() instanceof InstrumentItem instrumentItem) {
                    if (event.getAction() == InputConstants.PRESS) {
                        instrumentItem.startPlay(player, InstrumentKeyMapping.getPitch(i), 80);
                        player.startUsingItem(InteractionHand.MAIN_HAND);
                    } else if (event.getAction() == InputConstants.RELEASE) {
                        instrumentItem.stopPlay(player, InstrumentKeyMapping.getPitch(i));
                        player.stopUsingItem();
                    }
                }
            }
        }
    }
}
