package modist.romantictp.client.sound.efx;

import org.lwjgl.openal.EXTEfx;

import java.util.function.Consumer;

public enum ReverbType {
    EMPTY(i->{}),
    TEST(i -> {
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_GAIN, 1F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_GAINHF, 1F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_DECAY_TIME, 4F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN, 3.16F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN, 10.0F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF, 1.0F);
    }),
    TEST_2(i -> {
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_GAIN, 1F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_GAINHF, 1F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_DECAY_TIME, 20F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN, 3.16F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN, 10.0F);
        EXTEfx.alEffectf(i, EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF, 1.0F);
    });

    public static ReverbType fromString(String name){
        try {
            return valueOf(name);
        } catch (Exception e) {
            return EMPTY;
        }
    }

    public final Consumer<Integer> setReverb;

    ReverbType(Consumer<Integer> setReverb) {
        this.setReverb = setReverb;
    }
}
