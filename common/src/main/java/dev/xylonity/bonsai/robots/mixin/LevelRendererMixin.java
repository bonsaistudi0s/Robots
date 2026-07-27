package dev.xylonity.bonsai.robots.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips the world pass of a mech passenger, which is drawn on its seat bone by the mech rider layer instead
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void robots$skipMechPassenger(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource buffers, CallbackInfo ci) {
        if (entity.getVehicle() instanceof TankMechEntity) {
            ci.cancel();
        }

    }

}
