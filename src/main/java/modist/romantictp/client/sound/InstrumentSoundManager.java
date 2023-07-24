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

//1. management of sound instance, thus providing method starting sequence (close is managed by soundInstance tick automatically)
//2. sending midi message and broadcasting!
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

    public void startPlay(InstrumentPlayer player, int pitch, int velocity, boolean broadcast) {
        sendMessage(player, MidiHelper.startMessage(pitch, velocity), -1, broadcast);
    }

    public void stopPlay(InstrumentPlayer player, int pitch, boolean broadcast) {
        sendMessage(player, MidiHelper.stopMessage(pitch), -1, broadcast);
    }

    public void startSequence(InstrumentPlayer player, String name, boolean broadcast) { //TODO: move to "global receiver", here a simple method to access sequencer
        InstrumentSoundInstance soundInstance = getSound(player);
        Sequence sequence = MidiFileLoader.getInstance().getSequence(name);
        soundInstance.attachSequencer(sequence);
        if(broadcast){
            NetworkHandler.sendToServer(new InstrumentSoundPacket(player,name));
        }
    }
}
