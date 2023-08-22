package modist.romantictp.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import modist.romantictp.client.sound.NaturalTrumpetSoundInstance;
import modist.romantictp.client.sound.midi.LocalReceiver;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.common.item.ItemLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RomanticTp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void changeSound(PlaySoundSourceEvent event) {
        if (hasReverbHelmet() || event.getSound() instanceof NaturalTrumpetSoundInstance) { //natural trumpet should be SONARE
            EFXManager.getInstance().applyEFX(ReverbType.SUPER, event.getChannel().source);
        }
    }

    @SubscribeEvent
    public static void changeSound(PlayStreamingSourceEvent event) {
        if (hasReverbHelmet()) {
            EFXManager.getInstance().applyEFX(ReverbType.SUPER, event.getChannel().source);
        }
    }

    public static boolean hasReverbHelmet() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(ItemLoader.REVERB_HELMET.get());
    }

    @SubscribeEvent
    public static void startPlay(InputEvent.Key event) {
        for (int i = 0; i < InstrumentKeyMapping.PITCHES.size(); i++) {
            Lazy<KeyMapping> k = InstrumentKeyMapping.PITCHES.get(i);
            if (event.getKey() != -1 && event.getKey() == k.get().getKey().getValue()) {
                if (event.getAction() == InputConstants.PRESS && Minecraft.getInstance().screen == null) { //may be not available, e.g. chatting
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
