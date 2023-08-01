package modist.romantictp.client.sound;

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

//management of sound instance,
//thus providing method sending midi message and starting sequence and broadcasting (close is managed by soundInstance tick automatically)
public class InstrumentSoundManager {
    static InstrumentSoundManager instance = new InstrumentSoundManager();
    private final Map<InstrumentPlayer, InstrumentSoundInstance> soundInstanceCache = new ConcurrentHashMap<>();

    public static InstrumentSoundManager getInstance() {
        return instance;
    }

    @Nullable
    private InstrumentSoundInstance getSound(@NotNull InstrumentPlayer player) { //lazy
        InstrumentSoundInstance soundInstance = soundInstanceCache.get(player);
        if (soundInstance == null) {
            return tryCreate(player);
        } else {
            if(checkContains(soundInstance)){
                return soundInstance;
            } else {
                soundInstance.destroy();
                return tryCreate(player);
            }
        }
    }

    @Nullable
    private InstrumentSoundInstance tryCreate(InstrumentPlayer player) { //try to create. if muted, do nothing and return null
        InstrumentSoundInstance soundInstance = new InstrumentSoundInstance(player);
        Minecraft.getInstance().getSoundManager().play(soundInstance);
        if(checkContains(soundInstance)){
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
            NetworkHandler.sendToServer(new InstrumentSoundPacket(player, message, timeStamp));
        }
    }

    public void startSequence(@NotNull InstrumentPlayer player, byte[] midiData, boolean broadcast) {
        InstrumentSoundInstance soundInstance = getSound(player);
        if (soundInstance != null) {
            Sequence sequence = MidiHelper.loadSequence(midiData);
            if(sequence != null) {
                soundInstance.attachSequencer(sequence);
            }
        }
        if (broadcast) {
            NetworkHandler.sendToServer(new InstrumentSoundPacket(player, midiData));
        }
    }

    public void playNaturalTrumpet(LivingEntity player, boolean broadcast) {
        Minecraft.getInstance().getSoundManager().play(new NaturalTrumpetSoundInstance(player));
        if(broadcast) {
            NetworkHandler.sendToServer(new InstrumentSoundPacket(player));
        }
    }
}