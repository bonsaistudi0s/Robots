package dev.xylonity.bonsai.robots.mixin;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Moves the camera slightly to a top-right position
 */
@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void move(double forwards, double up, double left);

    @Inject(
            method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D")
    )
    private void robots$offsetThirdPersonCamera(BlockGetter level, Entity entity, boolean detached, boolean mirrored, float partialTick, CallbackInfo ci) {
        if (entity instanceof LocalPlayer player && player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player) {
            final float target = mech.isAiming() ? 1.0F : 0.0F;
            final float frameTicks = Minecraft.getInstance().getDeltaFrameTime();
            final float factor = 1.0F - (float) Math.pow(0.5D, frameTicks / 2.5f);
            mech.clientAimCameraProgress += (target - mech.clientAimCameraProgress) * factor;
            move(1.25d * mech.clientAimCameraProgress, 0, -0.85D);
        }

    }

}
