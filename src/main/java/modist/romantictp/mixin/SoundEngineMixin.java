package modist.romantictp.mixin;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.InstrumentSoundInstance;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.client.sound.efx.EFXManager;
import modist.romantictp.client.sound.loader.SynthesizerPool;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Shadow
    private boolean loaded;
    //protect instrument sound instance. only when stopAll or player dead can destroy
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
    public void destroy(CallbackInfo ci) {
        if (this.loaded) {
            InstrumentSoundManager.getInstance().destroy();
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

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        SynthesizerPool.SynthesizerWrapper.tick();
    }
}
