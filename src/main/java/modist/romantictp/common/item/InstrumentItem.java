package modist.romantictp.common.item;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.common.block.AutoPlayerBlock;
import modist.romantictp.common.block.InstrumentBlock;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class InstrumentItem extends BlockItem { //TODO right click to drop
    public final Supplier<Instrument> defaultInstrument;

    public InstrumentItem(InstrumentBlock block) {
        super(block, new Item.Properties().stacksTo(1));
        this.defaultInstrument = block.defaultInstrument;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000; //INFINITY
    }

    public void startPlay(LivingEntity player, int pitch, int volume) {
        RomanticTp.info("start play" + System.currentTimeMillis());
        if (player.level().isClientSide) {
            InstrumentSoundManager.getInstance().startPlay(InstrumentPlayerManager.getOrCreate(player), pitch, volume);
        }
    }

    public void stopPlay(LivingEntity player, int pitch, int volume) {
        if (player.level().isClientSide) {
            InstrumentSoundManager.getInstance().stopPlay(InstrumentPlayerManager.getOrCreate(player), pitch, volume);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.MAIN_HAND) {
            if(pPlayer.getOffhandItem().getItem() instanceof ScoreItem) {
                pPlayer.startUsingItem(pUsedHand);
                return InteractionResultHolder.consume(pPlayer.getMainHandItem());
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public Instrument getInstrument(ItemStack stack) {
        return stack.getTagElement("instrument") == null ? defaultInstrument.get() : new Instrument(stack.getTagElement("instrument"));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        Instrument instrument = getInstrument(pStack);
        instrument.addText(pTooltip, pIsAdvanced.isAdvanced());
    }

    public List<ItemStack> getDisplay() {
        ItemStack stack = new ItemStack(this);
        CompoundTag tag = defaultInstrument.get().serializeNBT();
        stack.addTagElement("instrument", tag);
        return List.of(stack);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        if(pContext.getClickedFace() == Direction.UP &&
                pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof AutoPlayerBlock) {
            return super.canPlace(pContext, pState);
        }
        return false;
    }
}
