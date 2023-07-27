package modist.romantictp.client.event;

import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.client.sound.loader.MidiKeyboardLoader;
import modist.romantictp.client.sound.loader.SynthesizerPool;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.sound.SoundEngineLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistryEventHandler {
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        InstrumentKeyMapping.PITCHES.forEach(l -> event.register(l.get()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) //avoid MIDI Keyboard conflict?
    public static void initAudio(FMLClientSetupEvent event) {
        MidiKeyboardLoader.getInstance().init();
    }

    @SubscribeEvent
    public static void loadResource(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(MidiFileLoader.getInstance());
        event.registerReloadListener(SynthesizerPool.getInstance());
    }
}
