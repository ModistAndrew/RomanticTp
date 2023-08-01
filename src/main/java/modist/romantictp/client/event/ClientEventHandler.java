package modist.romantictp.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.audio.LocalReceiver;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.ScoreItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.SoundEngineLoadEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = RomanticTp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void changeSound(PlaySoundSourceEvent event) {
        //EFXManager.getInstance().applyEFX(event.getChannel().source);
    }

    @SubscribeEvent
    public static void startPlay(InputEvent.Key event) {
        for (int i = 0; i < 7; i++) {
            Lazy<KeyMapping> k = InstrumentKeyMapping.PITCHES.get(i);
            if (event.getKey() != -1 && event.getKey() == k.get().getKey().getValue()) {
                if (event.getAction() == InputConstants.PRESS && Minecraft.getInstance().screen==null) { //may be not available, e.g. chatting
                    LocalReceiver.getInstance().send
                            (MidiHelper.startMessage(InstrumentKeyMapping.getPitch(i), 80), -1);
                } else if (event.getAction() == InputConstants.RELEASE) {
                    LocalReceiver.getInstance().send
                            (MidiHelper.stopMessage(InstrumentKeyMapping.getPitch(i)), -1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void startPlaySequence(UseItemEnd event) {
        if(event.getEntity().getOffhandItem().getItem() instanceof ScoreItem scoreItem) {
            InstrumentSoundManager.getInstance().startSequence(InstrumentPlayerManager.getOrCreate(event.getEntity()),
                    scoreItem.getMidiData(event.getEntity().getOffhandItem()), false);
        }
    }

    public static class UseItemEnd extends LivingEntityUseItemEvent.Start { //client only
        public UseItemEnd(LivingEntity entity, @NotNull ItemStack item, int duration) {
            super(entity, item, duration);
        }
    }
}
