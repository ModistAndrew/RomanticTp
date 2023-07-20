package modist.romantictp.common.item;

import modist.romantictp.RomanticTp;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemLoader {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RomanticTp.MODID);

    public static final RegistryObject<Item> TRUMPET = ITEMS.register("trumpet", () -> new TrumpetItem());
}
