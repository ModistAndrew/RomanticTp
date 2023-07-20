package modist.romantictp.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import modist.romantictp.client.audio.SynthesizerPool;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistryEventHandler {
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        InstrumentKeyMapping.PITCHES.forEach(l -> event.register(l.get()));
    }

    @SubscribeEvent
    public static void initAudio(FMLClientSetupEvent event) {
        SynthesizerPool.getInstance().init();
    }
}
