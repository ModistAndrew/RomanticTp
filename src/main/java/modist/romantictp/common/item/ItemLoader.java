package modist.romantictp.common.item;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.BlockLoader;
import modist.romantictp.common.entity.EntityLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemLoader {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RomanticTp.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RomanticTp.MODID);
    public static final Map<String, RegistryObject<InstrumentItem>> INSTRUMENTS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<Item>> MUSICIAN_BUSTS = new LinkedHashMap<>();
    public static final LazyOptional<List<ItemStack>> INSTRUMENT_LIST = LazyOptional.of(ItemLoader::createInstrumentList);
    public static final RegistryObject<ScoreItem> SCORE = ITEMS.register("score", ScoreItem::new);
    public static final RegistryObject<Item> AUTO_PLAYER = ITEMS.register("auto_player", () -> new BlockItem(BlockLoader.AUTO_PLAYER.get(), new Item.Properties()));
    public static final RegistryObject<Item> REVERB_HELMET = ITEMS.register("reverb_helmet", () ->
            new BlockItem(BlockLoader.REVERB_HELMET.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> NATURAL_TRUMPET = ITEMS.register("natural_trumpet", NaturalTrumpetItem::new);
    public static final RegistryObject<Item> MELODY_EGG = ITEMS.register("melody_spawn_egg", () -> new ForgeSpawnEggItem(EntityLoader.MELODY, 0xFFFF00, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<Item> SOUTH_SECTION_ONE = ITEMS.register("south_section_one", () -> new Item(new Item.Properties()));

    private static List<ItemStack> createInstrumentList() {
        List<ItemStack> list = new ArrayList<>();
        INSTRUMENTS.forEach((s, i) -> list.addAll(i.get().getDisplay()));
        return list;
    }

    static {
        BlockLoader.INSTRUMENTS.forEach((s, b) -> INSTRUMENTS.put
                (s, ITEMS.register(s, () -> new InstrumentItem(b.get()))));
        BlockLoader.MUSICIAN_BUSTS.forEach((s, b) -> MUSICIAN_BUSTS.put
                (s, ITEMS.register(s, () -> new BlockItem(b.get(), new Item.Properties().rarity(Rarity.RARE)))));
        TABS.register("romantictp_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.romantictp_tab"))
                .displayItems((parameters, output) -> {
                    INSTRUMENT_LIST.ifPresent(l -> l.forEach(output::accept));
                    SCORE.get().getDisplay().forEach(output::accept);
                    output.accept(AUTO_PLAYER.get());
                    output.accept(REVERB_HELMET.get());
                    output.accept(MELODY_EGG.get());
                    output.accept(NATURAL_TRUMPET.get());
                    MUSICIAN_BUSTS.values().forEach(i -> output.accept(i.get()));
                })
                .icon(() -> new ItemStack(SCORE.get()))
                .build());
    }
}