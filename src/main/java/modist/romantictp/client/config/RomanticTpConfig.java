package modist.romantictp.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RomanticTpConfig {
    public static ForgeConfigSpec CONFIG_SPEC;
    public static ForgeConfigSpec.ConfigValue<String> SOUNDBANK_LOCATION;
    public static ForgeConfigSpec.ConfigValue<String> MIDI_KEYBOARD;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SOUNDBANK_LOCATION = builder.comment("Optional full path to an .sf2 format soundbank to be used by the synthesizer")
                .define("soundbank_location", "");
        MIDI_KEYBOARD = builder.comment("Optional name of the midi keyboard for midi input")
                .define("midi_keyboard", "");
        CONFIG_SPEC = builder.build();
    }
}
