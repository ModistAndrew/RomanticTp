package modist.romantictp.client.sound.efx;

import org.lwjgl.openal.EXTEfx;

import java.util.HashMap;

public record ReverbType(String name, float volume, float density, float diffusion, float gain, float gainHF, float gainLF,
                         float decayTime, float decayHFRatio, float decayLFRatio, float reflectionsGain,
                         float reflectionsDelay, float[] reflectionsPan, float lateReverbGain, float lateReverbDelay,
                         float[] lateReverbPan, float echoTime, float echoDepth, float modulationTime,
                         float modulationDepth, float airAbsorptionGainHF, float hfReference, float lfReference,
                         float roomRollOffFactor, int decayHFLimit) {
    public static final HashMap<String, ReverbType> REVERB_TYPES = new HashMap<>();
    public static final ReverbType GENERIC = register(new ReverbType("generic", 1f, 1.0000f, 1.0000f, 0.3162f, 0.8913f, 1.0000f, 1.4900f, 0.8300f, 1.0000f, 0.0500f, 0.0070f, new float[]{0.0000f, 0.0000f, 0.0000f}, 1.2589f, 0.0110f, new float[]{0.0000f, 0.0000f, 0.0000f}, 0.2500f, 0.0000f, 0.2500f, 0.0000f, 0.9943f, 5000.0000f, 250.0000f, 0.0000f, 0x1));
    public static final ReverbType SUPER = register(new ReverbType("super", 10f, 1.0000f, 1.0000f, 0.3162f, 0.5623f, 1.0000f, 3.9200f, 0.7000f, 1.0000f, 0.2427f, 0.0200f, new float[]{0.0000f, 0.0000f, 0.0000f}, 0.9977f, 0.0290f, new float[]{0.0000f, 0.0000f, 0.0000f}, 0.2500f, 0.0000f, 0.2500f, 0.0000f, 0.9943f, 5000.0000f, 250.0000f, 0.0000f, 0x1));
    public static final ReverbType CONCERT_HALL = register(new ReverbType("concert_hall", 2f, 1.0000f, 1.0000f, 0.3162f, 0.5623f, 1.0000f, 3.9200f, 0.7000f, 1.0000f, 0.2427f, 0.0200f, new float[]{0.0000f, 0.0000f, 0.0000f}, 0.9977f, 0.0290f, new float[]{0.0000f, 0.0000f, 0.0000f}, 0.2500f, 0.0000f, 0.2500f, 0.0000f, 0.9943f, 5000.0000f, 250.0000f, 0.0000f, 0x1));

    public static ReverbType fromString(String name) {
        return REVERB_TYPES.getOrDefault(name, GENERIC);
    }

    public static ReverbType register(ReverbType type) {
        REVERB_TYPES.put(type.name, type);
        return type;
    }

    public void setReverb(int reverb) {
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_DENSITY, density);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_DIFFUSION, diffusion);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_GAIN, m(gain));
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_GAINHF, m(gainHF));
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_GAINLF, m(gainLF));
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_DECAY_TIME, decayTime);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_DECAY_HFRATIO, decayHFRatio);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_DECAY_LFRATIO, decayLFRatio);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN, m(reflectionsGain, 3.16F));
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_REFLECTIONS_DELAY, reflectionsDelay);
        EXTEfx.alEffectfv(reverb, EXTEfx.AL_EAXREVERB_REFLECTIONS_PAN, reflectionsPan);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN, m(lateReverbGain, 10.0F));
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_LATE_REVERB_DELAY, lateReverbDelay);
        EXTEfx.alEffectfv(reverb, EXTEfx.AL_EAXREVERB_LATE_REVERB_PAN, lateReverbPan);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_ECHO_TIME, echoTime);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_ECHO_DEPTH, echoDepth);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_MODULATION_TIME, modulationTime);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_MODULATION_DEPTH, modulationDepth);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF, m(airAbsorptionGainHF));
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_HFREFERENCE, hfReference);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_LFREFERENCE, lfReference);
        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_ROOM_ROLLOFF_FACTOR, 1F);
        EXTEfx.alEffecti(reverb, EXTEfx.AL_EAXREVERB_DECAY_HFLIMIT, decayHFLimit);

//        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_GAIN, 1F);
//        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_GAINHF, 1F);
//        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_DECAY_TIME, 20F);
//        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN, 3.16F);
//        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN, 10.0F);
//        EXTEfx.alEffectf(reverb, EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF, 1.0F);
    }

    private float m(float f, float max){
        return Math.min(max, f * volume);
    }

    private float m(float f){
        return m(f, 1);
    }
}
