package modist.romantictp.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import modist.romantictp.RomanticTp;
import modist.romantictp.common.entity.Melody;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class MelodyRenderer extends MobRenderer<Melody, MelodyModel> {
    private static final ResourceLocation MELODY_TEXTURE = new ResourceLocation(RomanticTp.MODID, "textures/entity/melody/melody.png");
    public MelodyRenderer(EntityRendererProvider.Context p_234551_) {
        super(p_234551_, new MelodyModel(p_234551_.bakeLayer(MelodyModel.MELODY)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this, p_234551_.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Melody pEntity) {
        return MELODY_TEXTURE;
    }

    @Override
    protected void scale(Melody pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        pMatrixStack.scale(2F, 2F, 2F);
    }
}
