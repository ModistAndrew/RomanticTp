package modist.romantictp.client.sound.efx;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.util.AlHelper;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;

import java.util.HashMap;

public class EFXManager {
    private final HashMap<ReverbType, Integer> auxFXSlots = new HashMap<>();
    private static EFXManager instance;

    public static void init() {
        instance = new EFXManager();
        AlHelper.checkALError();
    }

    public static EFXManager getInstance() {
        return instance;
    }

    private EFXManager() {
        //Get current context and device
        long currentContext = ALC10.alcGetCurrentContext();
        long currentDevice = ALC10.alcGetContextsDevice(currentContext);
        if (ALC10.alcIsExtensionPresent(currentDevice, "ALC_EXT_EFX")) {
            RomanticTp.LOGGER.info("EFX extension recognized");
        } else {
            RomanticTp.LOGGER.error("EFX extension not found on current device. Aborting.");
            return;
        }
        // Create auxiliary effect slots
        ReverbType.REVERB_TYPES.values().forEach(r -> {
            int slot = EXTEfx.alGenAuxiliaryEffectSlots();
            EXTEfx.alAuxiliaryEffectSloti(slot, EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL11.AL_TRUE);
            int reverb = EXTEfx.alGenEffects();
            EXTEfx.alEffecti(reverb, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_EAXREVERB);
            r.setReverb(reverb);
            EXTEfx.alAuxiliaryEffectSloti(slot, EXTEfx.AL_EFFECTSLOT_EFFECT, reverb);
            auxFXSlots.put(r, slot);
            RomanticTp.LOGGER.info("Aux slot {} created for reverb type {}", slot, r);
            AlHelper.checkALError();
        });
    }

    public void applyEFX(ReverbType type, int source) {
        AL11.alSource3i(source, EXTEfx.AL_AUXILIARY_SEND_FILTER, auxFXSlots.get(type), 0, EXTEfx.AL_FILTER_NULL);
        AlHelper.checkALError();
    }
}
