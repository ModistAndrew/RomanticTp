package modist.romantictp.mixin;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.audio.MyChannel;
import modist.romantictp.mixininterface.ICountingChannelPoolSpecial;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin({Library.CountingChannelPool.class})
public class CountingChannelPoolMixin implements ICountingChannelPoolSpecial {
    @Shadow
    @Final
    private int limit;
    @Shadow
    @Final
    private Set<Channel> activeChannels;


    @Override
    public Channel romanticTp$acquireSpecial() {
        if (this.activeChannels.size() >= this.limit) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                RomanticTp.LOGGER.warn("Maximum sound pool size {} reached", this.limit);
            }
            return null;
        } else {
            Channel channel = MyChannel.create();
            if (channel != null) {
                this.activeChannels.add(channel);
            }
            return channel;
        }
    }
}
