package modist.romantictp.network;

import modist.romantictp.client.sound.util.MidiInfo;
import modist.romantictp.common.event.CommonEventHandler;
import modist.romantictp.common.item.ScoreItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScoreSyncPacket {
    protected final int id;
    protected final MidiInfo info;

    public ScoreSyncPacket(int id, MidiInfo info) {
        this.id = id;
        this.info = info;
    }

    public ScoreSyncPacket(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readInt();
        this.info = new MidiInfo(byteBuf.readNbt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeNbt(this.info.serializeNBT());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        CommonEventHandler.setData(id, info);
    }
}