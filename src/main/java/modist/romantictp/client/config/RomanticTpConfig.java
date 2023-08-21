package modist.romantictp.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RomanticTpConfig {
    public static ForgeConfigSpec CONFIG_SPEC;
    public static ForgeConfigSpec.ConfigValue<String> SOUNDBANK_LOCATION;
    public static ForgeConfigSpec.ConfigValue<String> MIDI_KEYBOARD;
    public static ForgeConfigSpec.ConfigValue<Boolean> JITTER_CORRECTION;
    public static ForgeConfigSpec.ConfigValue<Integer> MAX_DISTANCE; //TODO: stored in auto player?
    public static ForgeConfigSpec.ConfigValue<Integer> SYNTHESIZER_POOL_SIZE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SOUNDBANK_LOCATION = builder.comment("Optional full path to an .sf2 format soundbank to be used by the synthesizer")
                .define("soundbank_location", "");
        MIDI_KEYBOARD = builder.comment("Optional name of the midi keyboard for midi input")
                .define("midi_keyboard", "");
        JITTER_CORRECTION = builder.comment("Jitter correction results in more stable tempo but longer delay in input")
                .comment("When enabled, any block in render thread can cause delay")
                .define("jitter_correction", true);
        MAX_DISTANCE = builder.comment("The radius of the area instrument sounds spread over")
                .define("max_distance", 32);
        SYNTHESIZER_POOL_SIZE = builder.comment("The most instruments to be loaded at a time")
                .define("initial_synthesizer_count", 64);
        CONFIG_SPEC = builder.build();
    }
}
