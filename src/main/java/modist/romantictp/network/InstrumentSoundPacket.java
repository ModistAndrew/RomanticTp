package modist.romantictp.network;

import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.util.MidiHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.sound.midi.ShortMessage;
import java.util.function.Supplier;

public class InstrumentSoundPacket {
    protected final int op;
    protected final ShortMessage midiMessage;
    protected final long timeStamp;
    protected final byte[] midiData;

    public InstrumentSoundPacket(ShortMessage message, long timeStamp){
        this.op = 0;
        this.midiMessage = message;
        this.timeStamp = timeStamp;
        this.midiData = new byte[0];
    }

    public InstrumentSoundPacket(byte[] midiData) {
        this.op = 1;
        this.midiMessage = new ShortMessage();
        this.timeStamp = 0;
        this.midiData = midiData;
    }

    public InstrumentSoundPacket() {
        this.op = 2;
        this.midiMessage = new ShortMessage();
        this.timeStamp = 0;
        this.midiData = new byte[0];
    }

    public InstrumentSoundPacket(FriendlyByteBuf byteBuf){
        this.op = byteBuf.readInt();
        this.midiMessage = MidiHelper.load(byteBuf);
        this.timeStamp = byteBuf.readLong();
        this.midiData = byteBuf.readByteArray();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.op);
        MidiHelper.save(buf, this.midiMessage);
        buf.writeLong(this.timeStamp);
        buf.writeByteArray(this.midiData);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender(); //should always be sender
        NetworkHandler.broadcast(player, player.getX(), player.getY(), player.getZ(), 64D, player.level().dimension(),
                new InstrumentSoundBroadcastPacket(this, player.getId()));
    }
}
