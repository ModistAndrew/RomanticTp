package modist.romantictp.common.item;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.BlockLoader;
import modist.romantictp.common.block.InstrumentBlock;
import modist.romantictp.common.entity.EntityLoader;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemLoader {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RomanticTp.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RomanticTp.MODID);

    public static final Map<String, RegistryObject<Item>> INSTRUMENTS = new HashMap<>();

    static {
        BlockLoader.INSTRUMENTS.forEach((s, b) -> INSTRUMENTS.put
                (s, ITEMS.register(s, () -> new InstrumentItem((InstrumentBlock) b.get()))));
    }

    public static final RegistryObject<Item> SCORE = ITEMS.register("score", ScoreItem::new);
    public static final RegistryObject<Item> AUTO_PLAYER = ITEMS.register("auto_player", () -> new BlockItem(BlockLoader.AUTO_PLAYER.get(), new Item.Properties()));
    public static final RegistryObject<Item> MELODY_EGG = ITEMS.register("melody", () -> new ForgeSpawnEggItem(EntityLoader.MELODY, 0xFFFF00, 0xFFFF99, new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> ROMANTICTP_TAB = TABS.register("romantictp_tab",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.romantictp_tab"))
            .displayItems((parameters, output) -> {
                INSTRUMENTS.forEach((s, i) -> ((InstrumentItem)i.get()).getDisplay().forEach(output::accept));
                ((ScoreItem)SCORE.get()).getDisplay().forEach(output::accept);
                output.accept(AUTO_PLAYER.get());
                output.accept(MELODY_EGG.get());
            })
            .icon(() -> new ItemStack(SCORE.get()))
            .build());
}