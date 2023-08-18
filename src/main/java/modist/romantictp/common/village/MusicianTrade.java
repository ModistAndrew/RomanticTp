package modist.romantictp.common.village;

import modist.romantictp.common.item.ItemLoader;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class MusicianTrade {
    public static final List<Item> DISCS = List.of(Items.MUSIC_DISC_11, Items.MUSIC_DISC_13,
            Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR,
            Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_STAL,
            Items.MUSIC_DISC_WAIT, Items.MUSIC_DISC_WARD);

    public static MerchantOffer sellInstrument(Entity pTrader, RandomSource pRandom) {
        List<ItemStack> stacks = ItemLoader.INSTRUMENT_LIST.orElse(null);
        ItemStack stack = stacks.get(pRandom.nextInt(stacks.size()));
        return new MerchantOffer
                (new ItemStack(Items.EMERALD, 36), stack, 1, 20, 0.2F);
    }

    public static MerchantOffer sellScore(Entity pTrader, RandomSource pRandom) {
        ItemStack stack = new ItemStack(ItemLoader.SCORE.get());
        return new MerchantOffer
                (new ItemStack(Items.EMERALD, 5), stack, 8, 5, 0.05F);
    }

    public static MerchantOffer sellBust(Entity pTrader, RandomSource pRandom) {
        List<RegistryObject<Item>> item = ItemLoader.MUSICIAN_BUSTS.values().stream().toList();
        ItemStack stack = new ItemStack(item.get(pRandom.nextInt(item.size())).get());
        return new MerchantOffer
                (new ItemStack(Items.EMERALD, 36), stack, 1, 40, 0.2F);
    }

    public static MerchantOffer buyPaper(Entity pTrader, RandomSource pRandom) {
        return new MerchantOffer(new ItemStack(Items.PAPER, 24), new ItemStack(Items.EMERALD), 16, 2, 0.05F);
    }

    public static MerchantOffer buyNoteBox(Entity pTrader, RandomSource pRandom) {
        return new MerchantOffer(new ItemStack(Items.NOTE_BLOCK), new ItemStack(Items.EMERALD), 16, 10, 0.05F);
    }

    public static MerchantOffer buyAmethyst(Entity pTrader, RandomSource pRandom) {
        return new MerchantOffer(new ItemStack(Items.AMETHYST_SHARD), new ItemStack(Items.EMERALD), 16, 30, 0.05F);
    }

    public static MerchantOffer buyDisc(Entity pTrader, RandomSource pRandom) {
        Item disc = DISCS.get(pRandom.nextInt(DISCS.size()));
        return new MerchantOffer(new ItemStack(disc), new ItemStack(Items.EMERALD, 36), 16, 30, 0.05F);
    }
}
