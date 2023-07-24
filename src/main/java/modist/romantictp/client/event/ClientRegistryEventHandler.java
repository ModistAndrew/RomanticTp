package modist.romantictp.client.event;

import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.client.sound.loader.SoundbankLoader;
import modist.romantictp.client.sound.audio.SynthesizerPool;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
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
        SoundbankLoader.getInstance().init(); //first load soundbank synchronously
        SynthesizerPool.getInstance().init();
        MidiFileLoader.getInstance().init();
    }
}
