package modist.romantictp.client.sound;

import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.network.InstrumentSoundPacket;
import modist.romantictp.network.NetworkHandler;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//management of sound instance,
//thus providing method sending midi message and starting sequence and broadcasting (close is managed by soundInstance tick automatically)
//TODO quit Game? ESC? where init? reload?
public class InstrumentSoundManager {
    static InstrumentSoundManager instance = new InstrumentSoundManager();
    private final Map<InstrumentPlayer, InstrumentSoundInstance> soundInstanceCache = new ConcurrentHashMap<>();

    public static InstrumentSoundManager getInstance() {
        return instance;
    }

    @Nullable
    private InstrumentSoundInstance getSound(InstrumentPlayer player) {
        InstrumentSoundInstance soundInstance = soundInstanceCache.get(player);
        if (soundInstance == null) { //lazy creation
            soundInstance = new InstrumentSoundInstance(player);
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            soundInstanceCache.put(player, soundInstance);
        }
        return soundInstance;
    }

    public void remove(InstrumentPlayer player) {
        soundInstanceCache.remove(player);
    }

    public void sendMessage(InstrumentPlayer player, ShortMessage message, long timeStamp, boolean broadcast) {
        InstrumentSoundInstance soundInstance = getSound(player);
        soundInstance.sendMessage(message, timeStamp);
        if(broadcast){
            NetworkHandler.sendToServer(new InstrumentSoundPacket(player, message, timeStamp));
        }
    }

    public void startSequence(InstrumentPlayer player, byte[] midiData, boolean broadcast) {
        InstrumentSoundInstance soundInstance = getSound(player);
        Sequence sequence = MidiHelper.loadSequence(midiData);
        soundInstance.attachSequencer(sequence);
        if(broadcast){
            NetworkHandler.sendToServer(new InstrumentSoundPacket(player, midiData));
        }
    }
}
