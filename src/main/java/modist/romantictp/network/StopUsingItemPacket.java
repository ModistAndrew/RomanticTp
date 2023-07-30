package modist.romantictp.network;

import modist.romantictp.common.item.InstrumentItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StopUsingItemPacket {
    protected final int id;

    public StopUsingItemPacket(LivingEntity entity){
        this.id = entity.getId();
    }

    public StopUsingItemPacket(FriendlyByteBuf byteBuf){
        this.id = byteBuf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        Level level = ctx.getSender().level();
        if(level.getEntity(id) instanceof LivingEntity entity && entity.isUsingItem() &&
        entity.getUsedItemHand() == InteractionHand.MAIN_HAND && entity.getUseItem().getItem() instanceof InstrumentItem) {
            entity.stopUsingItem();
        }
    }
}
