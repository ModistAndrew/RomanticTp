package modist.romantictp.mixin;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import modist.romantictp.mixininterface.IChannelAccessSpecial;
import modist.romantictp.mixininterface.ILibrarySpecial;
import net.minecraft.client.sounds.ChannelAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin({ChannelAccess.class})
public class ChannelAccessMixin implements IChannelAccessSpecial {
    @Shadow
    @Final
    private Set<ChannelAccess.ChannelHandle> channels = Sets.newIdentityHashSet();
    @Shadow
    @Final
    Library library;
    @Shadow
    @Final
    Executor executor;

    @Override
    public CompletableFuture<ChannelAccess.ChannelHandle> romanticTp$createHandleSpecial(Library.Pool pSystemMode) {
        CompletableFuture<ChannelAccess.ChannelHandle> completablefuture = new CompletableFuture<>();
        this.executor.execute(() -> {
            Channel channel = ((ILibrarySpecial)this.library).romanticTp$acquireChannelSpecial(pSystemMode);
            if (channel != null) {
                ChannelAccess.ChannelHandle channelaccess$channelhandle = ((ChannelAccess)(Object)this).new ChannelHandle(channel);
                this.channels.add(channelaccess$channelhandle);
                completablefuture.complete(channelaccess$channelhandle);
            } else {
                completablefuture.complete(null);
            }

        });
        return completablefuture;
    }
}
