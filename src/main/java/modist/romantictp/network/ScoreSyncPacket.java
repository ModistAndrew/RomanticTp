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

    public ScoreSyncPacket(int id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public ScoreSyncPacket(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readInt();
        this.data = byteBuf.readByteArray();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeByteArray(this.data);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        CommonEventHandler.setData(id, data);
    }
}