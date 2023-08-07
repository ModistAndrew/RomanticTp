package modist.romantictp.common.block;

import com.mojang.datafixers.DSL;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.instrument.Instruments;
import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.ItemLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BlockLoader {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RomanticTp.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RomanticTp.MODID);

    public static final Map<String, RegistryObject<InstrumentBlock>> INSTRUMENTS = new HashMap<>();
    static {
        registerInstrument("piano", Instrument.Builder.of(Instruments.PIANO).build());
        registerInstrument("timpani", Instrument.Builder.of(Instruments.TIMPANI).build());
        registerInstrument("music_box", Instrument.Builder.of(Instruments.MUSIC_BOX).build());
        registerInstrument("marimba", Instrument.Builder.of(Instruments.MARIMBA).build());
        registerInstrument("bell", Instrument.Builder.of(Instruments.BELL).build());
        registerInstrument("violin", Instrument.Builder.of(Instruments.VIOLIN).build());
        registerInstrument("viola", Instrument.Builder.of(Instruments.VIOLA).build());
        registerInstrument("cello", Instrument.Builder.of(Instruments.CELLO).build());
        registerInstrument("contrabass", Instrument.Builder.of(Instruments.CONTRABASS).build());
        registerInstrument("trumpet", Instrument.Builder.of(Instruments.TRUMPET).build(),
                Instrument.Builder.of(Instruments.TRUMPET).reverb(ReverbType.TEST).build());
        registerInstrument("trombone", Instrument.Builder.of(Instruments.TROMBONE).build());
        registerInstrument("tuba", Instrument.Builder.of(Instruments.TUBA).build());
        registerInstrument("french_horn", Instrument.Builder.of(Instruments.FRENCH_HORN).build());
        registerInstrument("soprano_sax", Instrument.Builder.of(Instruments.SOPRANO_SAX).build());
        registerInstrument("oboe", Instrument.Builder.of(Instruments.OBOE).build());
        registerInstrument("english_horn", Instrument.Builder.of(Instruments.ENGLISH_HORN).build());
        registerInstrument("bassoon", Instrument.Builder.of(Instruments.BASSOON).build());
        registerInstrument("clarinet", Instrument.Builder.of(Instruments.CLARINET).build());
        registerInstrument("piccolo", Instrument.Builder.of(Instruments.PICCOLO).build());
        registerInstrument("flute", Instrument.Builder.of(Instruments.FLUTE).build());
    }
    public static final RegistryObject<Block> AUTO_PLAYER = BLOCKS.register("auto_player", AutoPlayerBlock::new);
    public static final RegistryObject<Block> REVERB_HELMET = BLOCKS.register("reverb_helmet", ReverbHelmetBlock::new);
    public static final Map<String, RegistryObject<MusicianBustBlock>> MUSICIAN_BUSTS = new HashMap<>();
    static {
        registerMusicianBust("bach");
        registerMusicianBust("beethoven");
        registerMusicianBust("mozart");
        registerMusicianBust("mahler");
        registerMusicianBust("haydn");
    }

    public static final RegistryObject<BlockEntityType<InstrumentBlockEntity>> INSTRUMENT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("instrument_block_entity", ()-> BlockEntityType.Builder.of
                    (InstrumentBlockEntity::new, INSTRUMENTS.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<AutoPlayerBlockEntity>> AUTO_PLAYER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("auto_player_block_entity", ()-> BlockEntityType.Builder.of
                    (AutoPlayerBlockEntity::new, AUTO_PLAYER.get()).build(DSL.remainderType()));

    private static void registerInstrument(String name, Instrument... instrument){
        INSTRUMENTS.put(name, BLOCKS.register(name, () -> new InstrumentBlock(instrument[0], List.of(instrument))));
    }

    private static void registerMusicianBust(String name){
        MUSICIAN_BUSTS.put(name, BLOCKS.register(name, MusicianBustBlock::new));
    }
}