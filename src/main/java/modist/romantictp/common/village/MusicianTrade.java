package modist.romantictp.common.village;

import modist.romantictp.common.item.ItemLoader;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MusicianTrade {
    public static MerchantOffer getInstrument(Entity pTrader, RandomSource pRandom) {
        List<ItemStack> stacks = ItemLoader.INSTRUMENT_LIST.orElse(null);
        stacks.add(new ItemStack(ItemLoader.NATURAL_TRUMPET.get()));
        ItemStack stack = stacks.get(pRandom.nextInt(stacks.size()));
        return new MerchantOffer
                (new ItemStack(Items.EMERALD, 20), stack, 1, 20, 0.2F);
    }

    public static MerchantOffer getScore(Entity pTrader, RandomSource pRandom) {
        ItemStack stack = new ItemStack(ItemLoader.SCORE.get());
        return new MerchantOffer
                (new ItemStack(Items.EMERALD, 5), stack, 8, 5, 0.2F);
    }

    public static MerchantOffer getBust(Entity pTrader, RandomSource pRandom) {
        List<RegistryObject<Item>> item = ItemLoader.MUSICIAN_BUSTS.values().stream().toList();
        item.add(ItemLoader.REVERB_HELMET);
        ItemStack stack = new ItemStack(item.get(pRandom.nextInt(item.size())).get());
        return new MerchantOffer
                (new ItemStack(Items.EMERALD, 36), stack, 12, 30, 0.2F);
    }
}
