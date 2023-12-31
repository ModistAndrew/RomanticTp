package modist.romantictp.common.block;

import com.mojang.datafixers.DSL;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.instrument.Instruments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockLoader {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RomanticTp.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RomanticTp.MODID);

    private static final boolean[] USED_INSTRUMENTS = new boolean[128];

    public static final Map<String, RegistryObject<InstrumentBlock>> INSTRUMENTS = new LinkedHashMap<>();

    static {
        registerInstrument("all", Instrument.ALL);
        registerInstrument("timpani", Instrument.Builder.of(Instruments.TIMPANI).build());
        registerInstrument("music_box", Instrument.Builder.of(Instruments.MUSIC_BOX).build());
        registerInstrument("marimba", Instrument.Builder.of(Instruments.MARIMBA).build());
        registerInstrument("bell", Instrument.Builder.of(Instruments.BELL).build());
        registerInstrument("violin", Instrument.Builder.of(Instruments.VIOLIN).build());
        registerInstrument("viola", Instrument.Builder.of(Instruments.VIOLA).build());
        registerInstrument("cello", Instrument.Builder.of(Instruments.CELLO).build());
        registerInstrument("contrabass", Instrument.Builder.of(Instruments.CONTRABASS).build());
        registerInstrument("strings", Instrument.Builder.of(Instruments.STRINGS).build());
        registerInstrument("trumpet", Instrument.Builder.of(Instruments.TRUMPET).build());
        registerInstrument("trombone", Instrument.Builder.of(Instruments.TROMBONE).build());
        registerInstrument("tuba", Instrument.Builder.of(Instruments.TUBA).build());
        registerInstrument("french_horn", Instrument.Builder.of(Instruments.FRENCH_HORN).build());
        registerInstrument("brass_section", Instrument.Builder.of(Instruments.BRASS_SECTION).build());
        registerInstrument("soprano_sax", Instrument.Builder.of(Instruments.SOPRANO_SAX).build());
        registerInstrument("oboe", Instrument.Builder.of(Instruments.OBOE).build());
        registerInstrument("english_horn", Instrument.Builder.of(Instruments.ENGLISH_HORN).build());
        registerInstrument("bassoon", Instrument.Builder.of(Instruments.BASSOON).build());
        registerInstrument("clarinet", Instrument.Builder.of(Instruments.CLARINET).build());
        registerInstrument("piccolo", Instrument.Builder.of(Instruments.PICCOLO).build());
        registerInstrument("flute", Instrument.Builder.of(Instruments.FLUTE).build());
        registerInstrument("organ", Instrument.Builder.of(Instruments.ORGAN).build());
        registerInstrument("harp", Instrument.Builder.of(Instruments.HARP).build());
        registerInstrument("harpsichord", Instrument.Builder.of(Instruments.HARPSICHORD).build());
        registerInstrument("piano", Instrument.Builder.of(Instruments.PIANO).build(),
                Instrument.Builder.of(Instruments.PIANO).reverb(ReverbType.CONCERT_HALL).build());
        registerInstrument("guitar", Instrument.Builder.of(Instruments.GUITAR).build());
        registerInstrument("bass", Instrument.Builder.of(Instruments.BASS).build());
        registerInstrument("drum_kit", Instrument.Builder.of(Instruments.DRUM_KIT).build());
        for (int i = 0; i < 128; i++) {
            if(!USED_INSTRUMENTS[i]){
                registerInstrument(true, "default_instrument_"+i, Instrument.Builder.of(i).build());
            }
        }
    }

    public static final RegistryObject<Block> AUTO_PLAYER = BLOCKS.register("auto_player", AutoPlayerBlock::new);
    public static final RegistryObject<Block> REVERB_HELMET = BLOCKS.register("reverb_helmet", ReverbHelmetBlock::new);
    public static final Map<String, RegistryObject<MusicianBustBlock>> MUSICIAN_BUSTS = new LinkedHashMap<>();

    static {
        registerMusicianBust("bach");
        registerMusicianBust("beethoven");
        registerMusicianBust("brahms");
        registerMusicianBust("chopin");
        registerMusicianBust("debussy");
        registerMusicianBust("dvorak");
        registerMusicianBust("elgar");
        registerMusicianBust("handel");
        registerMusicianBust("haydn");
        registerMusicianBust("liszt");
        registerMusicianBust("mahler");
        registerMusicianBust("mozart");
        registerMusicianBust("rachmaninoff");
        registerMusicianBust("ravel");
        registerMusicianBust("satie");
        registerMusicianBust("schubert");
        registerMusicianBust("schumann");
        registerMusicianBust("shostakovich");
        registerMusicianBust("sibelius");
        registerMusicianBust("stravinsky");
        registerMusicianBust("tchaikovsky");
        registerMusicianBust("vivaldi");
    }

    public static final RegistryObject<BlockEntityType<InstrumentBlockEntity>> INSTRUMENT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("instrument_block_entity", () -> BlockEntityType.Builder.of
                    (InstrumentBlockEntity::new, INSTRUMENTS.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<AutoPlayerBlockEntity>> AUTO_PLAYER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("auto_player_block_entity", () -> BlockEntityType.Builder.of
                    (AutoPlayerBlockEntity::new, AUTO_PLAYER.get()).build(DSL.remainderType()));

    private static void registerInstrument(boolean isDefault, String name, Instrument... instrument) {
        int id = instrument[0].instrumentId();
        if (id >= 0 && id < 128 && !isDefault) {
            USED_INSTRUMENTS[id] = true;
        }
        INSTRUMENTS.put(name, BLOCKS.register(name, () -> new InstrumentBlock(instrument[0],
                isDefault ? List.of() : List.of(instrument))));
    }

    private static void registerInstrument(String name, Instrument... instrument) {
        registerInstrument(false, name, instrument);
    }

    public static boolean hasInstrument(int id) {
        if (id >= 0 && id < 128) {
            return USED_INSTRUMENTS[id];
        }
        return false;
    }

    private static void registerMusicianBust(String name) {
        MUSICIAN_BUSTS.put(name, BLOCKS.register(name, MusicianBustBlock::new));
    }
}