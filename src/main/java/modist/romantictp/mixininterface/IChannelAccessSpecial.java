package modist.romantictp.mixininterface;

import com.mojang.blaze3d.audio.Library;
import net.minecraft.client.sounds.ChannelAccess;

import java.util.concurrent.CompletableFuture;

public interface IChannelAccessSpecial {
    CompletableFuture<ChannelAccess.ChannelHandle> romanticTp$createHandleSpecial(Library.Pool pSystemMode);
}
