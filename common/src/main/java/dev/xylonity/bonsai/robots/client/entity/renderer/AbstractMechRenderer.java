package dev.xylonity.bonsai.robots.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.api.util.KnightLibMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class AbstractMechRenderer<T extends AbstractMechEntity> extends GeoEntityRenderer<T> {

    protected float torsoYawHalfLife = 1.5f;
    protected float legsYawHalfLife = 4.0f;

    // Minimal distance considered as movement
    protected double moveThresholdSqr = 1.0E-5;

    protected AbstractMechRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    // The death animation itself handles the pose; no need for GeckoLib's vanilla fall-over rotation
    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0.0F;
    }

    // Avoids the red damage-flash overlay tinting the model while the death animation plays.
    // Checked against isDeadOrDying() rather than deathTime, since deathTime only starts
    // incrementing a tick after death, which left a 1-frame flash right as the death anim kicks in
    @Override
    public int getPackedOverlay(T animatable, float u, float partialTick) {
        if (animatable.isDeadOrDying()) {
            return OverlayTexture.NO_OVERLAY;
        }

        return super.getPackedOverlay(animatable, u, partialTick);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        updateVisualYaw(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        if (animatable.clientYawInitialized) {
            rotationYaw = animatable.clientLegsYaw;
        }

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    private void updateVisualYaw(T mech, float partialTick) {
        final Player player = mech.getPilot();
        if (player == null) {
            return;
        }

        // The torso follows the camera yaw
        final float lookYaw = player.getViewYRot(partialTick);
        if (!mech.clientYawInitialized) {
            mech.clientLegsYaw = lookYaw;
            mech.clientTorsoYaw = lookYaw;
            mech.clientLegsReversed = false;
            mech.clientYawInitialized = true;
            return;
        }

        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused()) {
            return;
        }

        final float frameTicks = minecraft.getDeltaFrameTime();
        mech.clientTorsoYaw = KnightLibMath.lerpAngle(mech.clientTorsoYaw, lookYaw, smoothFactor(frameTicks, torsoYawHalfLife));

        final double dx = mech.getX() - mech.xo;
        final double dz = mech.getZ() - mech.zo;
        if (dx * dx + dz * dz > moveThresholdSqr) {
            final float moveYaw = KnightLibMath.yawAngleOf(new Vec3(dx, 0.0D, dz));

            // Rotates the legs to the front if they are reversed and realigns them anyway
            if (Math.abs(KnightLibMath.angleDelta(lookYaw, moveYaw)) < 80.0F) {
                mech.clientLegsReversed = false;
            }
            else {
                // Inverse leg directional rotation
                final float delta = Math.abs(KnightLibMath.angleDelta(mech.clientLegsYaw, moveYaw));
                if (mech.clientLegsReversed ? delta < 75.0F : delta > 105.0F) {
                    mech.clientLegsReversed = !mech.clientLegsReversed;
                }

            }

            final float targetYaw = mech.clientLegsReversed ? moveYaw + 180.0F : moveYaw;
            mech.clientLegsYaw = KnightLibMath.lerpAngle(mech.clientLegsYaw, targetYaw, smoothFactor(frameTicks, legsYawHalfLife));
        }

    }

    private static float smoothFactor(float frameTicks, float halfLifeTicks) {
        return 1.0f - (float) Math.pow(0.5, frameTicks / halfLifeTicks);
    }

}