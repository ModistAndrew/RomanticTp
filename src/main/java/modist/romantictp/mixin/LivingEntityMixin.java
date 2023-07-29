package modist.romantictp.mixin;

import modist.romantictp.client.event.ClientEventHandler;
import modist.romantictp.client.sound.efx.EFXManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand pHand);

    @Inject(method = "startUsingItem", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;useItemRemaining:I"))
    private void startUsingItem(InteractionHand pHand, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        if(livingEntity.level().isClientSide) {
            ItemStack itemstack = this.getItemInHand(pHand);
            MinecraftForge.EVENT_BUS.post(new ClientEventHandler.UseItemEnd(livingEntity, itemstack, itemstack.getUseDuration()));
        }
    }
}
