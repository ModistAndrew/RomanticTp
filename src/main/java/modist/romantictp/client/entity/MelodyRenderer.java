package modist.romantictp.client.entity;

import modist.romantictp.RomanticTp;
import net.minecraft.client.renderer.entity.AllayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;

public class MelodyRenderer extends AllayRenderer {
    private static final ResourceLocation MELODY_TEXTURE = new ResourceLocation(RomanticTp.MODID, "textures/entity/melody/melody.png");
    public MelodyRenderer(EntityRendererProvider.Context p_234551_) {
        super(p_234551_);
    }

    @Override
    public ResourceLocation getTextureLocation(Allay p_234558_) {
        return MELODY_TEXTURE;
    }
}
