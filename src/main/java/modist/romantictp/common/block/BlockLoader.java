package modist.romantictp.common.block;

import com.mojang.datafixers.DSL;
import modist.romantictp.RomanticTp;
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
        registerInstrument("trumpet", () -> new Instrument(1F,1F, 62, true));
    }

    private static void registerInstrument(String name, Supplier<Instrument> instrument){
        INSTRUMENTS.put(name, BLOCKS.register(name, () -> new InstrumentBlock(instrument)));
    }

    public static final RegistryObject<BlockEntityType<InstrumentBlockEntity>> INSTRUMENT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("instrument_block_entity", ()-> BlockEntityType.Builder.of
                    (InstrumentBlockEntity::new, INSTRUMENTS.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(DSL.remainderType()));
}