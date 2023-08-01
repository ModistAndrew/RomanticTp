package modist.romantictp.client.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import modist.romantictp.common.block.AutoPlayerBlock;
import modist.romantictp.common.block.AutoPlayerBlockEntity;
import modist.romantictp.common.block.InstrumentBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;

public class AutoPlayerRenderer implements BlockEntityRenderer<AutoPlayerBlockEntity> {
    private final ItemRenderer itemRenderer;

    public AutoPlayerRenderer(ItemRenderer itemRenderer) {
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(AutoPlayerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.5D, 0.5D);
        if(pBlockEntity.containsScore()) {
            if(pBlockEntity.isPlaying()) {
                long time = System.currentTimeMillis();
                float angle = InstrumentRenderer.getAngle(time, 20);
                float trans = InstrumentRenderer.diffFunction(time, 1000, 0.0002F);
                pPoseStack.translate(0, trans, 0);
                pPoseStack.mulPose(new Quaternionf().rotationY(angle));
            }
            itemRenderer.renderStatic(new ItemStack(Items.APPLE), ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay,
                    pPoseStack, pBuffer, pBlockEntity.getLevel(), 0);
        }
        pPoseStack.popPose();
    }
}
