package modist.romantictp.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.audio.LocalReceiver;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.common.item.InstrumentItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.SoundEngineLoadEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void changeSound(PlaySoundSourceEvent event) {
        //EFXManager.getInstance().applyEFX(event.getChannel().source);
    }

    @SubscribeEvent
    public static void startPlay(InputEvent.Key event) {
        for (int i = 0; i < 7; i++) {
            Lazy<KeyMapping> k = InstrumentKeyMapping.PITCHES.get(i);
            if (event.getKey() == k.get().getKey().getValue()) {
                if (event.getAction() == InputConstants.PRESS) {
                    LocalReceiver.getInstance().send
                            (MidiHelper.startMessage(InstrumentKeyMapping.getPitch(i), 80), -1);
                } else if (event.getAction() == InputConstants.RELEASE) {
                    LocalReceiver.getInstance().send
                            (MidiHelper.stopMessage(InstrumentKeyMapping.getPitch(i)), -1);
                }
            }
        }
    }
}
