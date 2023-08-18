package modist.romantictp.client.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.AutoPlayerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;

public class AutoPlayerRenderer implements BlockEntityRenderer<AutoPlayerBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;
    public static final ResourceLocation DISC_LOCATION = new ResourceLocation(RomanticTp.MODID, "block/disc");

    public AutoPlayerRenderer(BlockRenderDispatcher blockRenderer) {
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(AutoPlayerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.containsScore()) {
            pPoseStack.pushPose();
            if (pBlockEntity.isPlaying()) {
                pPoseStack.translate(0.5D, 0.5D, 0.5D);
                long time = System.currentTimeMillis();
                float angle = InstrumentRenderer.getAngle(time, 5);
                pPoseStack.mulPose(new Quaternionf().rotationY(angle));
                pPoseStack.translate(-0.5D, -0.5D, -0.5D);
            }
            blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), pBuffer.getBuffer(Sheets.cutoutBlockSheet()),
                    null, blockRenderer.getBlockModelShaper().getModelManager().getModel(DISC_LOCATION), 1F, 1F, 1F,
                    pPackedLight, pPackedOverlay, ModelData.EMPTY, null);
            pPoseStack.popPose();
            //TODO: show progress
        }
    }
}
