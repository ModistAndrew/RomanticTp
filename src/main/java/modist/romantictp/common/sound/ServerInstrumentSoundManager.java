package modist.romantictp.common.sound;

import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.client.sound.InstrumentSoundInstance;
import modist.romantictp.client.sound.NaturalTrumpetSoundInstance;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.network.InstrumentSoundBroadcastPacket;
import modist.romantictp.network.InstrumentSoundPacket;
import modist.romantictp.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//except block entity which auto synchronizes itself to client and keyboard input, all sound should be played from here
public class ServerInstrumentSoundManager {
    static ServerInstrumentSoundManager instance = new ServerInstrumentSoundManager();

    public static ServerInstrumentSoundManager getInstance() {
        return instance;
    }

    public void startSequence(@NotNull LivingEntity entity, byte[] midiData) {
        NetworkHandler.broadcast(null, entity,
                new InstrumentSoundBroadcastPacket(new InstrumentSoundPacket(midiData), entity.getId()));
    }

    public void playNaturalTrumpet(@NotNull LivingEntity entity) {
        NetworkHandler.broadcast(null, entity,
                new InstrumentSoundBroadcastPacket(new InstrumentSoundPacket(), entity.getId()));
    }
}