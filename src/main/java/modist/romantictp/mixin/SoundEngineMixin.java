package modist.romantictp.mixin;

import com.mojang.blaze3d.audio.Library;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.AlChannel;
import modist.romantictp.client.sound.InstrumentSoundInstance;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.mixininterface.IChannelAccessSpecial;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Shadow
    private boolean loaded;
    //protect instrument sound instance. only when stopAll or player dead can destroy
    @Redirect(
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/sounds/ChannelAccess;createHandle(Lcom/mojang/blaze3d/audio/Library$Pool;)Ljava/util/concurrent/CompletableFuture;"
            ),
            method = "play"
    )
    public CompletableFuture<ChannelAccess.ChannelHandle> playSpecial(ChannelAccess channelAccess, Library.Pool pSystemMode, SoundInstance p_120313_) {
        if(p_120313_ instanceof InstrumentSoundInstance instance){
            CompletableFuture<ChannelAccess.ChannelHandle> ret = ((IChannelAccessSpecial)channelAccess).romanticTp$createHandleSpecial(pSystemMode);
            ret.join().execute(channel -> instance.bindChannel((AlChannel) channel));
            return ret;
        }
        return channelAccess.createHandle(pSystemMode);
    }

    @Redirect(
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/sounds/Sound;getAttenuationDistance()I"
            ),
            method = "play"
    )
    public int changeAttenuationDistance(Sound instance) {
        if(instance.getLocation().equals(new ResourceLocation("romantictp:blank"))) {
            return RomanticTpConfig.MAX_DISTANCE.get();
        }
        return instance.getAttenuationDistance();
    }

    @Redirect(method = "tickNonPaused", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z"))
    public boolean remove(List<SoundInstance> list, Object instance) {
        if(instance instanceof SoundInstance && !(instance instanceof InstrumentSoundInstance)) {
            return list.remove(instance);
        }
        return false;
    }

    @Inject(method = "loadLibrary", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Listener;reset()V"))
    private void loadLibrary(CallbackInfo ci) { //we have to mixin to get the right context for EFX initiation
        EFXManager.init();
    }

    @Inject(method = "stopAll", at = @At("HEAD"))
    public void stopAll(CallbackInfo ci) {
        if (this.loaded) {
            InstrumentSoundManager.getInstance().stopAll();
        }
    }

    @Inject(method = "pause", at = @At("HEAD"))
    public void pause(CallbackInfo ci) {
        if (this.loaded) {
            InstrumentSoundManager.getInstance().pause();
        }
    }

    @Inject(method = "resume", at = @At("HEAD"))
    public void resume(CallbackInfo ci) {
        if (this.loaded) {
            InstrumentSoundManager.getInstance().resume();
        }
    }
}
