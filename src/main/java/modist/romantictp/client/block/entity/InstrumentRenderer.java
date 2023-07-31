package modist.romantictp.client.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import modist.romantictp.common.block.InstrumentBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class InstrumentRenderer implements BlockEntityRenderer<InstrumentBlockEntity> {
    private final ItemRenderer itemRenderer;

    public InstrumentRenderer(ItemRenderer itemRenderer) {
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(InstrumentBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.3D, 0.5D);
        ItemStack itemStack = pBlockEntity.getInstrument();
        long time = System.currentTimeMillis();
        float angle = getAngle(time, 20);
        float trans = diffFunction(time, 1000, 0.0002F);
        pPoseStack.translate(0, trans, 0);
        pPoseStack.mulPose(new Quaternionf().rotationY(angle));
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay,
                pPoseStack, pBuffer, pBlockEntity.getLevel(), 0);
        pPoseStack.popPose();
    }
    private static float diffFunction(long time, long delta, float scale) {
        long dt = time % (delta * 2);
        if (dt > delta) {
            dt = 2 * delta - dt;
        }
        return dt * scale;
    }

    private static float getAngle(long time, long delta) {
        return (float) ((time / delta) % 360 / 180F * Math.PI);
    }
}
