package modist.romantictp.network;

import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.InstrumentSoundManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

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

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::doHandle));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public void doHandle() { //check distance on client
        Entity entity = Minecraft.getInstance().level.getEntity(playerId);
        if(entity instanceof LivingEntity player && player.distanceTo(Minecraft.getInstance().player) <= RomanticTpConfig.MAX_DISTANCE.get()) {
            switch (packet.op) {
                case 0 -> InstrumentSoundManager.getInstance().sendMessage(InstrumentPlayerManager.getOrCreate(player), packet.midiMessage, packet.timeStamp, false);
                case 1 -> InstrumentSoundManager.getInstance().attachSequence(InstrumentPlayerManager.getOrCreate(player), packet.midiData, false);
                case 2 -> InstrumentSoundManager.getInstance().playNaturalTrumpet(player, false);
            }
        }
    }
}
