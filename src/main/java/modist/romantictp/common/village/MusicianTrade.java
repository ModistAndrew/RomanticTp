package modist.romantictp.common.village;

import modist.romantictp.common.item.ItemLoader;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.ArrayList;
import java.util.List;

public class MusicianTrade {
    public static MerchantOffer getInstrument(Entity pTrader, RandomSource pRandom) {
        List<ItemStack> stacks = ItemLoader.INSTRUMENT_LIST.orElse(null);
        ItemStack stack = stacks.get(pRandom.nextInt(stacks.size()));
        return new MerchantOffer
                (new ItemStack(Items.EMERALD, 10), stack, 12, 10, 0.2F);
    }

    public static MerchantOffer getScore(Entity pTrader, RandomSource pRandom) {
        List<ItemStack> stacks = ItemLoader.SCORE_LIST.orElse(null);
        ItemStack stack = stacks.get(pRandom.nextInt(stacks.size()));
        return new MerchantOffer
                (new ItemStack(Items.EMERALD, 5), stack, 12, 10, 0.2F);
    }
}
