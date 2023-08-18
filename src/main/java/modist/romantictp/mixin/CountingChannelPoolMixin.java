package modist.romantictp.mixin;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import modist.romantictp.client.sound.AlChannel;
import modist.romantictp.mixininterface.ICountingChannelPoolSpecial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin({Library.CountingChannelPool.class})
public class CountingChannelPoolMixin implements ICountingChannelPoolSpecial {
    @Shadow
    @Final
    private Set<Channel> activeChannels;


    @Override
    public Channel romanticTp$acquireSpecial() {
        Channel channel = AlChannel.create();
        if (channel != null) {
            this.activeChannels.add(channel);
        }
        return channel;
    }
}
