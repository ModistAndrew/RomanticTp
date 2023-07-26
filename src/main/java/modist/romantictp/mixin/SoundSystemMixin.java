package modist.romantictp.mixin;

import modist.romantictp.client.sound.efx.EFXManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SoundEngine.class})
public class SoundSystemMixin {
    private final Minecraft minecraft = Minecraft.getInstance();

    @Inject(method = "loadLibrary", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Listener;reset()V")) //TODO: move to a event?
    private void loadLibrary(CallbackInfo ci) {
        EFXManager.init();
    }
}
