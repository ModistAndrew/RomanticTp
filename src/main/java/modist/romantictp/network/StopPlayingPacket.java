package modist.romantictp.network;

import modist.romantictp.common.block.AutoPlayerBlockEntity;
import modist.romantictp.common.item.InstrumentItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StopPlayingPacket {
    protected final BlockPos pos;

    public StopPlayingPacket(BlockEntity entity){
        this.pos = entity.getBlockPos();
    }

    public StopPlayingPacket(FriendlyByteBuf byteBuf){
        this.pos = byteBuf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        Level level = ctx.getSender().level();
        if(level.getBlockEntity(pos) instanceof AutoPlayerBlockEntity blockEntity) {
            blockEntity.stopPlaying();
        }
    }
}
