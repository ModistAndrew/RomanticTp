package modist.romantictp.common.block;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ReverbHelmetBlock extends Block implements Equipable {
    public ReverbHelmetBlock() {
        super(BlockBehaviour.Properties.of().instabreak());
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}
