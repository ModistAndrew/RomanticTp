package modist.romantictp.network;

import modist.romantictp.common.event.CommonEventHandler;
import modist.romantictp.common.item.ScoreItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScoreSyncPacket {
    protected final int id;
    protected final byte[] data;
    protected final long time;

    public ScoreSyncPacket(int id, byte[] data, long time) {
        this.id = id;
        this.data = data;
        this.time = time;
    }

    public ScoreSyncPacket(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readInt();
        this.data = byteBuf.readByteArray();
        this.time = byteBuf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeByteArray(this.data);
        buf.writeLong(this.time);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        CommonEventHandler.setData(id, data, time);
    }
}