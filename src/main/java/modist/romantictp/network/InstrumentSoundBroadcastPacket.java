package modist.romantictp.network;

import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.util.MidiHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.sound.midi.ShortMessage;
import java.util.function.Supplier;

public class InstrumentSoundBroadcastPacket {
    private final InstrumentSoundPacket packet;

    public InstrumentSoundBroadcastPacket(InstrumentSoundPacket packet){
        this.packet = packet;
    }
    public InstrumentSoundBroadcastPacket(FriendlyByteBuf byteBuf){
        this.packet = new InstrumentSoundPacket(byteBuf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        packet.toBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        InstrumentPlayer player = InstrumentPlayerManager.fromNbt(packet.intrumentPlayer);
        switch (packet.op) {
            case 0 -> InstrumentSoundManager.getInstance().sendMessage(player, packet.midiMessage, packet.timeStamp, false);
            case 1 -> InstrumentSoundManager.getInstance().startSequence(player, packet.sequenceName, false);
        }
    }
}
