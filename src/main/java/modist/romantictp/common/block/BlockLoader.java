package modist.romantictp.common.block;

import com.mojang.datafixers.DSL;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.common.instrument.Instrument;
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

    public static final Map<String, RegistryObject<Block>> INSTRUMENTS = new HashMap<>();
    static {
        registerInstrument("trumpet", new Instrument(0,1F, 62, true, ReverbType.EMPTY),
                            List.of(new Instrument(0,1F, 62, true, ReverbType.EMPTY),
                                    new Instrument(0,1F, 62, true, ReverbType.TEST)));
    }
    public static final RegistryObject<Block> AUTO_PLAYER = BLOCKS.register("auto_player", AutoPlayerBlock::new);
    public static final RegistryObject<Block> REVERB_HELMET = BLOCKS.register("reverb_helmet", ReverbHelmetBlock::new);

    public static final RegistryObject<BlockEntityType<InstrumentBlockEntity>> INSTRUMENT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("instrument_block_entity", ()-> BlockEntityType.Builder.of
                    (InstrumentBlockEntity::new, INSTRUMENTS.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<AutoPlayerBlockEntity>> AUTO_PLAYER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("auto_player_block_entity", ()-> BlockEntityType.Builder.of
                    (AutoPlayerBlockEntity::new, AUTO_PLAYER.get()).build(DSL.remainderType()));

    private static void registerInstrument(String name, Instrument instrument){
        INSTRUMENTS.put(name, BLOCKS.register(name, () -> new InstrumentBlock(instrument)));
    }

    private static void registerInstrument(String name, Instrument instrument, List<Instrument> display){
        INSTRUMENTS.put(name, BLOCKS.register(name, () -> new InstrumentBlock(instrument, display)));
    }
}