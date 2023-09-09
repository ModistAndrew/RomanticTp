package modist.romantictp.common.item;

import modist.romantictp.client.item.InstrumentAnim;
import modist.romantictp.common.block.AutoPlayerBlock;
import modist.romantictp.common.block.InstrumentBlock;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.util.ItemDisplayProvider;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class InstrumentItem extends BlockItem implements ItemDisplayProvider {
    public final Instrument defaultInstrument;
    public final List<Instrument> display;

    public InstrumentItem(InstrumentBlock block) {
        super(block, new Item.Properties().stacksTo(1).rarity(block.defaultInstrument.isAll() ? Rarity.UNCOMMON : Rarity.COMMON));
        this.defaultInstrument = block.defaultInstrument;
        this.display = block.display;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000; //INFINITY
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        InteractionResult interactionresult = this.place(new BlockPlaceContext(pContext));
        if (!interactionresult.consumesAction()) { //can use
            return this.use(pContext.getLevel(), pContext.getPlayer(), pContext.getHand()).getResult();
        } else {
            return interactionresult;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.MAIN_HAND && pPlayer.getOffhandItem().getItem() instanceof ScoreItem) {
            pPlayer.startUsingItem(pUsedHand); //see server event
            return InteractionResultHolder.success(pPlayer.getMainHandItem());
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public Instrument getInstrument(ItemStack stack) {
        return stack.getTagElement("instrument") == null ? defaultInstrument : new Instrument(stack.getTagElement("instrument"));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        Instrument instrument = getInstrument(pStack);
        instrument.addText(pTooltip, pIsAdvanced.isAdvanced());
    }

    @Override
    public List<ItemStack> getDisplay() {
        return this.display.stream().map(instrument -> {
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = instrument.serializeNBT();
            stack.addTagElement("instrument", tag);
            return stack;
        }).toList();
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        if (pContext.getClickedFace() == Direction.UP &&
                pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof AutoPlayerBlock) {
            return super.canPlace(pContext, pState);
        }
        return false;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new InstrumentAnim());
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return getInstrument(pStack).isSpecial();
    }
}
