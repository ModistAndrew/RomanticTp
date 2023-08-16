package modist.romantictp.network;

import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.common.event.CommonEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScoreSyncPacket {
    protected final int id;
    protected final MidiHelper.MidiInfo info;

    public ScoreSyncPacket(int id, MidiHelper.MidiInfo info) {
        this.id = id;
        this.info = info;
    }

    public ScoreSyncPacket(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readInt();
        this.info = new MidiHelper.MidiInfo(byteBuf.readNbt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeNbt(this.info.serializeNBT());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        CommonEventHandler.setData(id, info);
    }
}