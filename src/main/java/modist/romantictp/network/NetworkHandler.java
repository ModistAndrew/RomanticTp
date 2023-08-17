package modist.romantictp.network;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.util.AudioHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;

public class NetworkHandler {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void registerMessage() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RomanticTp.MODID, "network"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE.messageBuilder(InstrumentSoundPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(InstrumentSoundPacket::new)
                .encoder(InstrumentSoundPacket::toBytes)
                .consumerMainThread(InstrumentSoundPacket::handle)
                .add();

        INSTANCE.messageBuilder(InstrumentSoundBroadcastPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(InstrumentSoundBroadcastPacket::new)
                .encoder(InstrumentSoundBroadcastPacket::toBytes)
                .consumerMainThread(InstrumentSoundBroadcastPacket::handle)
                .add();

        INSTANCE.messageBuilder(ScoreSyncPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ScoreSyncPacket::new)
                .encoder(ScoreSyncPacket::toBytes)
                .consumerMainThread(ScoreSyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(UseItemPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(UseItemPacket::new)
                .encoder(UseItemPacket::toBytes)
                .consumerMainThread(UseItemPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG packet) {
        INSTANCE.sendToServer(packet);
    }

    public static <MSG> void broadcast(@Nullable ServerPlayer pExcept, LivingEntity entity, MSG packet) {
        INSTANCE.send(PacketDistributor.NEAR.with
                (() -> new PacketDistributor.TargetPoint
                        (pExcept, entity.getX(), entity.getY(), entity.getZ(), AudioHelper.MAX_DISTANCE, entity.level().dimension())), packet);
    }
}
