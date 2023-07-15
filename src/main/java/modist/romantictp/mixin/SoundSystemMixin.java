package modist.romantictp.mixin;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.InstrumentSoundManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;
@Mixin({SoundEngine.class})
public class SoundSystemMixin {
    private final Minecraft minecraft = Minecraft.getInstance();


    @Inject(method = "loadLibrary", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Listener;reset()V"))
    private void loadLibrary(CallbackInfo ci) {
        InstrumentSoundManager.init();
    }

    @Inject(
            method = {"tickNonPaused"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Options;getSoundSourceVolume(Lnet/minecraft/sounds/SoundSource;)F"
            )},
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void tickNonPaused(CallbackInfo ci, Iterator<?> iterator, Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> map, ChannelAccess.ChannelHandle channelHandle, SoundInstance sound) {
        //RomanticTp.LOGGER.info(sound.toString());
            if (this.minecraft.level != null) {
                //RomanticTp.LOGGER.info("adaed");
                channelHandle.execute((channel) -> {
                    //InstrumentSoundManager.applyEFX(((ChannelAccessor) channel).getSource());
                });
            }
    }
}
