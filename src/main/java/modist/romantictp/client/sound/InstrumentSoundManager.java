package modist.romantictp.client.sound;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;
import org.slf4j.Logger;

import java.util.Random;

public class InstrumentSoundManager {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static int auxFXSlot;
    private static int reverb;
    private static int directFilter;
    private static int sendFilter;
    private static Minecraft mc;
    private static int maxAuxSends;
    public static Random random = new Random();

    public static void init(){
        setEFX();
        mc = Minecraft.getInstance();
    }

    private static void setEFX(){
        //Get current context and device
        long currentContext = ALC10.alcGetCurrentContext();
        long currentDevice = ALC10.alcGetContextsDevice(currentContext);
        if (ALC10.alcIsExtensionPresent(currentDevice, "ALC_EXT_EFX")) {
            LOGGER.info("EFX Extension recognized");
        } else {
            LOGGER.error("EFX Extension not found on current device. Aborting.");
            return;
        }

        maxAuxSends = ALC10.alcGetInteger(currentDevice, EXTEfx.ALC_MAX_AUXILIARY_SENDS);
        LOGGER.info("Max auxiliary sends: {}", maxAuxSends);

        // Create auxiliary effect slots
        auxFXSlot = EXTEfx.alGenAuxiliaryEffectSlots();
        LOGGER.info("Aux slot {} created", auxFXSlot);
        EXTEfx.alAuxiliaryEffectSloti(auxFXSlot, EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL11.AL_TRUE);

        reverb = EXTEfx.alGenEffects();
        EXTEfx.alEffecti(reverb, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_EAXREVERB);

        directFilter = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(directFilter, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);

        sendFilter = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(sendFilter, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);

        setReverbParams(auxFXSlot, reverb);
    }

    protected static void setReverbParams(int auxFXSlot, int reverbSlot) {
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DENSITY, 1F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DIFFUSION, 1F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_GAIN, 0.4F * 0.7F * 0.85F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_GAINHF, 0.89F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DECAY_TIME, 4.142F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DECAY_HFRATIO, 0.7F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN, 2.5F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN, 1.26F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_LATE_REVERB_DELAY, 0.021F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF, 0.994F);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_ROOM_ROLLOFF_FACTOR, 0.11F);
        // Attach updated effect object
        EXTEfx.alAuxiliaryEffectSloti(auxFXSlot, EXTEfx.AL_EFFECTSLOT_EFFECT, reverbSlot);
    }

    public static void applyEFX(int source) {
        EXTEfx.alFilterf(sendFilter, EXTEfx.AL_LOWPASS_GAIN, 1F);
        EXTEfx.alFilterf(sendFilter, EXTEfx.AL_LOWPASS_GAINHF, 1F);
        AL11.alSource3i(source, EXTEfx.AL_AUXILIARY_SEND_FILTER, auxFXSlot, 0, EXTEfx.AL_FILTER_NULL);
        //AL10.alSourcef(source, AL10.AL_PITCH, 2.0F);
    }
}
