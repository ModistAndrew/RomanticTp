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

public class InstrumentSoundPacket {
    protected final int op;
    protected final CompoundTag intrumentPlayer;
    protected final Vec3 position; //used to determine targets
    protected final ShortMessage midiMessage;
    protected final long timeStamp;
    protected final byte[] midiData;

    public InstrumentSoundPacket(InstrumentPlayer player, ShortMessage message, long timeStamp){
        this.op = 0;
        this.intrumentPlayer = player.serializeNBT();
        this.position = player.getPos();
        this.midiMessage = message;
        this.timeStamp = timeStamp;
        this.midiData = new byte[0];
    }

    public InstrumentSoundPacket(InstrumentPlayer player, byte[] midiData){
        this.op = 0;
        this.intrumentPlayer = player.serializeNBT();
        this.position = player.getPos();
        this.midiMessage = new ShortMessage();
        this.timeStamp = 0;
        this.midiData = midiData;
    }

    public InstrumentSoundPacket(FriendlyByteBuf byteBuf){
        this.op = byteBuf.readInt();
        this.intrumentPlayer = byteBuf.readNbt();
        double x = byteBuf.readDouble();
        double y = byteBuf.readDouble();
        double z = byteBuf.readDouble();
        position = new Vec3(x, y, z);
        this.midiMessage = MidiHelper.load(byteBuf);
        this.timeStamp = byteBuf.readLong();
        this.midiData = byteBuf.readByteArray();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.op);
        buf.writeNbt(this.intrumentPlayer);
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        buf.writeDouble(position.z);
        MidiHelper.save(buf, this.midiMessage);
        buf.writeLong(this.timeStamp);
        buf.writeByteArray(this.midiData);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        NetworkHandler.broadcast(player, position.x, position.y, position.z, 64D, player.level().dimension(),
                new InstrumentSoundBroadcastPacket(this));
    }
}
