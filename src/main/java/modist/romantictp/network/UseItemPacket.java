package modist.romantictp.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UseItemPacket {
    private final int id;
    private final boolean use;
    private final boolean isOffHand;


    public UseItemPacket(int id, boolean use, boolean isOffHand) {
        this.id = id;
        this.use = use;
        this.isOffHand = isOffHand;
    }

    public UseItemPacket(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readInt();
        this.use = byteBuf.readBoolean();
        this.isOffHand = byteBuf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.use);
        buf.writeBoolean(this.isOffHand);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if(entity instanceof LivingEntity livingEntity) {
            if(use){
                livingEntity.startUsingItem(isOffHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            } else {
                livingEntity.stopUsingItem();
            }
        }
    }
}
