package modist.romantictp.mixin;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import modist.romantictp.mixininterface.ICountingChannelPoolSpecial;
import modist.romantictp.mixininterface.ILibrarySpecial;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Library.class})
public class LibraryMixin implements ILibrarySpecial {
    @Shadow
    private Library.ChannelPool staticChannels;
    @Shadow
    private Library.ChannelPool streamingChannels;
    @Nullable
    @Override
    public Channel romanticTp$acquireChannelSpecial(Library.Pool pPool) {
        Library.ChannelPool channelPool = pPool == Library.Pool.STREAMING ? this.streamingChannels : this.staticChannels;
        if(channelPool instanceof ICountingChannelPoolSpecial special){
            return special.romanticTp$acquireSpecial();
        }
        return channelPool.acquire();
    }
}
