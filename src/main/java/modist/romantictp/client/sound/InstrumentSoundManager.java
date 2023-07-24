package modist.romantictp.client.sound;

import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.client.instrument.InstrumentPlayer;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//management of sound instance and provide method sending midi message and starting sequence (close is managed by soundInstance tick automatically)
//TODO quit Game? ESC? where init?
public class InstrumentSoundManager {
    static InstrumentSoundManager instance = new InstrumentSoundManager();
    //now playing, use instrument UUID to map
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

    public void remove(InstrumentPlayer player) { //TODO: removal of Sequencer and Player(optional?), destroy of channel(syn) and here!(for np)
        soundInstanceCache.remove(player);
    }

    public void sendMessage(InstrumentPlayer player, MidiMessage message, long timeStamp) {
        InstrumentSoundInstance soundInstance = getSound(player);
        soundInstance.sendMessage(message, timeStamp);
    }

    public void startPlay(InstrumentPlayer player, int pitch, int velocity) {
        sendMessage(player, MidiHelper.startMessage(1, pitch, velocity), -1);
    }

    public void stopPlay(InstrumentPlayer player, int pitch) {
        sendMessage(player, MidiHelper.stopMessage(1, pitch), -1);
    }

    public void startSequence(InstrumentPlayer player, String name) {
        InstrumentSoundInstance soundInstance = getSound(player);
        Sequence sequence = MidiFileLoader.getInstance().getSequence(name);
        soundInstance.attachSequencer(sequence);
    }
}
