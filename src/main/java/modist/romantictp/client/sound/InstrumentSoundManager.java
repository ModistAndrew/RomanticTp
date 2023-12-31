package modist.romantictp.client.sound;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.loader.SynthesizerPool;
import modist.romantictp.client.sound.loader.SynthesizerWrapper;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.network.InstrumentSoundPacket;
import modist.romantictp.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//management of sound instance, creating, pausing, resuming, destroying
//thus providing method sending midi message and starting sequence and broadcasting (close is managed by soundInstance tick automatically)
public class InstrumentSoundManager {
    private static final InstrumentSoundManager instance = new InstrumentSoundManager();
    private final Map<InstrumentPlayer, InstrumentSoundInstance> soundInstanceCache = new ConcurrentHashMap<>();

    public static InstrumentSoundManager getInstance() {
        return instance;
    }

    @Nullable
    private InstrumentSoundInstance getSound(@NotNull InstrumentPlayer player) { //lazy
        InstrumentSoundInstance soundInstance = soundInstanceCache.get(player);
        return (soundInstance != null && checkContains(soundInstance)) ? soundInstance : tryCreate(player);
    }

    @Nullable
    private InstrumentSoundInstance tryCreate(InstrumentPlayer player) { //try to create and put. if failed, do nothing and return null
        SynthesizerWrapper synthesizerWrapper = SynthesizerPool.getInstance().request();
        if(synthesizerWrapper == null) {
            RomanticTp.LOGGER.warn("Synthesizer out of limit.");
            return null;
        }
        InstrumentSoundInstance soundInstance = new InstrumentSoundInstance(player, synthesizerWrapper);
        Minecraft.getInstance().getSoundManager().play(soundInstance);
        if (checkContains(soundInstance)) { //may fail
            soundInstanceCache.put(player, soundInstance);
            return soundInstance;
        }
        return null;
    }

    private boolean checkContains(InstrumentSoundInstance soundInstance) {
        return Minecraft.getInstance().getSoundManager().soundEngine.tickingSounds.contains(soundInstance);
    }

    public void remove(InstrumentPlayer player) {
        soundInstanceCache.remove(player);
    }

    public void sendMessage(@NotNull InstrumentPlayer player, ShortMessage message, long timeStamp, boolean broadcast) {
        InstrumentSoundInstance soundInstance = getSound(player);
        if (soundInstance != null) {
            soundInstance.sendMessage(message, timeStamp);
        }
        if (broadcast) {
            NetworkHandler.sendToServer(new InstrumentSoundPacket(message, timeStamp));
        }
    }

    public void attachSequence(@NotNull InstrumentPlayer player, byte[] midiData, boolean broadcast) {
        InstrumentSoundInstance soundInstance = getSound(player);
        if (soundInstance != null) {
            Sequence sequence = MidiHelper.loadSequence(midiData);
            if (sequence != null) {
                soundInstance.attachSequencer(sequence);
            }
        }
        if (broadcast) {
            NetworkHandler.sendToServer(new InstrumentSoundPacket(midiData));
        }
    }

    public void playNaturalTrumpet(LivingEntity player, boolean broadcast) {
        Minecraft.getInstance().getSoundManager().play(new NaturalTrumpetSoundInstance(player));
        if (broadcast) {
            NetworkHandler.sendToServer(new InstrumentSoundPacket());
        }
    }

    public void stopAll() {
        soundInstanceCache.values().forEach(InstrumentSoundInstance::destroy);
    }

    public void pause() {
        soundInstanceCache.values().forEach(InstrumentSoundInstance::pause);
    }

    public void resume() {
        soundInstanceCache.values().forEach(InstrumentSoundInstance::unpause);
    }
}