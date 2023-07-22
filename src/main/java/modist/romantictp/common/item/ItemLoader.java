package modist.romantictp.common.item;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.BlockLoader;
import modist.romantictp.common.block.InstrumentBlock;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemLoader {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RomanticTp.MODID);

    public static final Map<String, RegistryObject<Item>> INSTRUMENTS = new HashMap<>();

    static {
        BlockLoader.INSTRUMENTS.forEach((s, b) -> INSTRUMENTS.put
                (s, ITEMS.register(s, () -> new InstrumentItem((InstrumentBlock) b.get()))));
    }

    public static final RegistryObject<Item> SCORE = ITEMS.register("score", ScoreItem::new);
}
