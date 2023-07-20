package modist.romantictp.common.item;

import modist.romantictp.common.instrument.TrumpetInstrument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;

public class TrumpetItem extends InstrumentItem<TrumpetInstrument> {
    public TrumpetItem() {
        super(TrumpetInstrument::new, TrumpetInstrument::new);
    }
}
