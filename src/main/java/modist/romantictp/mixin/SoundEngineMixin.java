package modist.romantictp.mixin;

import com.mojang.blaze3d.audio.Library;
import modist.romantictp.client.sound.InstrumentSoundInstance;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.mixininterface.IChannelAccessSpecial;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Redirect(
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/sounds/ChannelAccess;createHandle(Lcom/mojang/blaze3d/audio/Library$Pool;)Ljava/util/concurrent/CompletableFuture;"
            ),
            method = "play"
    )
    public CompletableFuture<ChannelAccess.ChannelHandle> playSpecial(ChannelAccess channelAccess, Library.Pool pSystemMode, SoundInstance p_120313_) {
        if(p_120313_ instanceof InstrumentSoundInstance instance){
            CompletableFuture<ChannelAccess.ChannelHandle> ret = ((IChannelAccessSpecial)channelAccess).romanticTp$createHandleSpecial(pSystemMode);
            instance.setChannel(ret.join());
            return ret;
        }
        return channelAccess.createHandle(pSystemMode);
    }

    @Inject(method = "loadLibrary", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Listener;reset()V"))
    private void loadLibrary(CallbackInfo ci) { //we have to mixin to get the right context for EFX initiation
        EFXManager.init();
    }
}
