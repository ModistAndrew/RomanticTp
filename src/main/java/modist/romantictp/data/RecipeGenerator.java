package modist.romantictp.data;

import modist.romantictp.common.item.ItemLoader;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemLoader.AUTO_PLAYER.get())
                .pattern("gdg")
                .pattern("gxg")
                .pattern("ggg")
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('x', Blocks.JUKEBOX)
                .define('g', Tags.Items.GLASS_COLORLESS)
                .unlockedBy("has_jukebox", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(Blocks.JUKEBOX).build()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemLoader.SCORE.get(), 8)
                .pattern("ppp")
                .pattern("pdp")
                .pattern("ppp")
                .define('d', ItemTags.MUSIC_DISCS)
                .define('p', Items.PAPER)
                .unlockedBy("has_music_discs", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(ItemTags.MUSIC_DISCS).build()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemLoader.INSTRUMENTS.get("piano").get())
                .pattern("iqi")
                .pattern("iqi")
                .pattern("nnn")
                .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
                .define('n', Blocks.NOTE_BLOCK)
                .unlockedBy("has_note_block", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(Blocks.NOTE_BLOCK).build()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemLoader.INSTRUMENTS.get("violin").get())
                .pattern("ssb")
                .pattern("www")
                .pattern("nnn")
                .define('s', Tags.Items.STRING)
                .define('b', Tags.Items.TOOLS_BOWS)
                .define('w', ItemTags.DARK_OAK_LOGS)
                .define('n', Blocks.NOTE_BLOCK)
                .unlockedBy("has_note_block", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(Blocks.NOTE_BLOCK).build()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemLoader.INSTRUMENTS.get("trumpet").get())
                .pattern("bbb")
                .pattern("ggg")
                .pattern("nnn")
                .define('b', ItemTags.STONE_BUTTONS)
                .define('g', Tags.Items.STORAGE_BLOCKS_GOLD)
                .define('n', Blocks.NOTE_BLOCK)
                .unlockedBy("has_note_block", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(Blocks.NOTE_BLOCK).build()))
                .save(consumer);
    }
}