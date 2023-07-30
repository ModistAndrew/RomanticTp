package modist.romantictp.client.sound.efx;

import modist.romantictp.RomanticTp;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;

public class EFXManager {
    private int auxFXSlot;
    private int reverb;
    private int directFilter;
    private int sendFilter;
    private int maxAuxSends;

    private static EFXManager instance;

    public static void init() {
        instance = new EFXManager();
    }

    public static EFXManager getInstance() {
        return instance;
    }

    private EFXManager() {
        //Get current context and device
        long currentContext = ALC10.alcGetCurrentContext();
        long currentDevice = ALC10.alcGetContextsDevice(currentContext);
        if (ALC10.alcIsExtensionPresent(currentDevice, "ALC_EXT_EFX")) {
            RomanticTp.LOGGER.info("EFX Extension recognized");
        } else {
            RomanticTp.LOGGER.error("EFX Extension not found on current device. Aborting.");
            return;
        }

        maxAuxSends = ALC10.alcGetInteger(currentDevice, EXTEfx.ALC_MAX_AUXILIARY_SENDS);
        RomanticTp.LOGGER.info("Max auxiliary sends: {}", maxAuxSends);

        // Create auxiliary effect slots
        auxFXSlot = EXTEfx.alGenAuxiliaryEffectSlots();
        RomanticTp.LOGGER.info("Aux slot {} created", auxFXSlot);
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

    public void applyEFX(ReverbType type, int source) {
        EXTEfx.alFilterf(sendFilter, EXTEfx.AL_LOWPASS_GAIN, 1F);
        EXTEfx.alFilterf(sendFilter, EXTEfx.AL_LOWPASS_GAINHF, 1F);
        AL11.alSource3i(source, EXTEfx.AL_AUXILIARY_SEND_FILTER, type == ReverbType.EMPTY ?
                EXTEfx.AL_EFFECT_NULL : auxFXSlot, 0, EXTEfx.AL_FILTER_NULL);
    }
}
