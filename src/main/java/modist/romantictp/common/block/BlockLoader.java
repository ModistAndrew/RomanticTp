package modist.romantictp.common.block;

import com.mojang.datafixers.DSL;
import modist.romantictp.RomanticTp;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockLoader {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RomanticTp.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RomanticTp.MODID);

    public static final RegistryObject<Block> INSTRUMENT = BLOCKS.register("instrument", InstrumentBlock::new);

    public static final RegistryObject<BlockEntityType<InstrumentBlockEntity>> INSTRUMENT_BLOCK_ENTITY =
            fromBlock(INSTRUMENT, InstrumentBlockEntity::new);

    @SuppressWarnings("ConstantConditions")
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> fromBlock(RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<T> pFactory) {
        return BLOCK_ENTITIES.register(block.getId().getPath()+"_block_entity", ()-> BlockEntityType.Builder.of
                (pFactory, block.get()).build(DSL.remainderType()));
    }
}
