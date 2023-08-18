package modist.romantictp.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RomanticTpConfig {
    public static ForgeConfigSpec CONFIG_SPEC;
    public static ForgeConfigSpec.ConfigValue<String> SOUNDBANK_LOCATION;
    public static ForgeConfigSpec.ConfigValue<String> MIDI_KEYBOARD;
    public static ForgeConfigSpec.ConfigValue<Boolean> JITTER_CORRECTION;
    public static ForgeConfigSpec.ConfigValue<Integer> MAX_DISTANCE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SOUNDBANK_LOCATION = builder.comment("Optional full path to an .sf2 format soundbank to be used by the synthesizer")
                .define("soundbank_location", "");
        MIDI_KEYBOARD = builder.comment("Optional name of the midi keyboard for midi input")
                .define("midi_keyboard", "");
        JITTER_CORRECTION = builder.comment("Jitter correction results in more stable tempo but longer delay")
                .define("jitter_correction", true);
        MAX_DISTANCE = builder.comment("The radius of the area instrument sounds spread over")
                .define("max_distance", 32);
        CONFIG_SPEC = builder.build();
    }
}
