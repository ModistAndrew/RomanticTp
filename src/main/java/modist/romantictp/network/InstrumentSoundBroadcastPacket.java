package modist.romantictp.network;

import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.util.MidiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.sound.midi.ShortMessage;
import java.util.function.Supplier;

public class InstrumentSoundBroadcastPacket {
    private final InstrumentSoundPacket packet;
    private final int playerId;

    public InstrumentSoundBroadcastPacket(InstrumentSoundPacket packet, int playerId) {
        this.packet = packet;
        this.playerId = playerId;
    }
    public InstrumentSoundBroadcastPacket(FriendlyByteBuf byteBuf) {
        this.packet = new InstrumentSoundPacket(byteBuf);
        this.playerId = byteBuf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        packet.toBytes(buf);
        buf.writeInt(this.playerId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        Entity entity = Minecraft.getInstance().level.getEntity(playerId);
        if(entity instanceof LivingEntity player) {
            switch (packet.op) {
                case 0 -> InstrumentSoundManager.getInstance().sendMessage(InstrumentPlayerManager.getOrCreate(player), packet.midiMessage, packet.timeStamp, false);
                case 1 -> InstrumentSoundManager.getInstance().startSequence(InstrumentPlayerManager.getOrCreate(player), packet.midiData, false);
                case 2 -> InstrumentSoundManager.getInstance().playNaturalTrumpet(player, false);
            }
        }
    }
}
