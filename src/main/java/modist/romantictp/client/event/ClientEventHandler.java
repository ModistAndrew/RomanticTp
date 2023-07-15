package modist.romantictp.client.event;

import modist.romantictp.client.keymap.InstrumentKeyMapping;
import modist.romantictp.client.sound.InstrumentSoundManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void changeSound(PlaySoundSourceEvent event) {
            InstrumentSoundManager.applyEFX(event.getChannel().source);
    }
}
