package modist.romantictp.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RomanticTpConfig {
    public static ForgeConfigSpec CONFIG_SPEC;
    public static ForgeConfigSpec.ConfigValue<String> SOUNDBANK;
    public static ForgeConfigSpec.ConfigValue<String> MIDI_KEYBOARD;
    public static ForgeConfigSpec.ConfigValue<Boolean> JITTER_CORRECTION;
    public static ForgeConfigSpec.ConfigValue<Integer> MAX_DISTANCE; //TODO: stored in auto player?
    public static ForgeConfigSpec.ConfigValue<Integer> MAX_LOAD_DISTANCE;
    public static ForgeConfigSpec.ConfigValue<Integer> SYNTHESIZER_POOL_SIZE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("You may reload resources using F3 + T after the config has been changed");
        builder.push("settings");
        SOUNDBANK = builder.comment("Path to an .sf2 or .sf3 format soundbank to be used by the synthesizer")
                .comment("Use the full path or simply the soundbank file name in assets/[NAMESPACE]/soundbank/")
                .comment("A default soundbank named romantictp.sf3 is available")
                .define("soundbank", "romantictp.sf3");
        MIDI_KEYBOARD = builder.comment("Optional name of the midi keyboard for midi input")
                .comment("You may check out available devices in the game log. Search \"available midi input device\"")
                .comment("Check whether other programs occupy the keyboard if it fails to load")
                .define("midi_keyboard", "");
        JITTER_CORRECTION = builder.comment("Jitter correction results in more stable tempo but longer delay in input, especially when you play notes")
                .comment("When enabled, any block in render thread can cause delay, but you may reload using F3 + T")
                .define("jitter_correction", true);
        MAX_DISTANCE = builder.comment("The radius of the area instrument sounds spread over")
                .define("max_distance", 32);
        MAX_LOAD_DISTANCE = builder.comment("The max distance from which instrument sounds can be loaded and unloaded")
                .define("max_load_distance", 48);
        SYNTHESIZER_POOL_SIZE = builder.comment("The most instruments to be loaded at a time")
                .define("synthesizer_pool_size", 64);
        builder.pop();
        CONFIG_SPEC = builder.build();
    }
}
