package dev.xylonity.bonsai.robots.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.api.util.KnightLibMath;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.knightlib.client.animation.renderer.KnightLibMobRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMechRenderer<T extends AbstractMechEntity> extends KnightLibMobRenderer<T> {

    public static final String TORSO_BONE = "body_player_control";

    protected float torsoYawHalfLife = 1.5f;
    protected float legsYawHalfLife = 4.0f;

    // Minimal distance considered as movement
    protected double moveThresholdSqr = 1.0E-5;

    public AbstractMechRenderer(EntityRendererProvider.Context context, float shadowRadius) {
        super(context, shadowRadius);
    }

    /**
     * The yaw state has to be refreshed before the model pose and the body rotation are evaluated, else both read the values of the previous frame
     */
    @Override
    protected void beforeRender(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        updatedPlayerControlledBoneYaw(entity, partialTick);
    }

    /**
     * The whole model faces where the mech walks
     */
    @Override
    protected void setupRotations(T entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        if (entity.clientYawInitialized) {
            rotationYaw = entity.clientLegsYaw;
        }

        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    /**
     * Counter rotates the torso back towards the camera, so it turns independently of the legs
     */
    @Override
    protected void setupPose(T entity, KnightLibModel model, float partialTick) {
        if (!entity.clientYawInitialized || !model.hasBone(TORSO_BONE)) {
            return;
        }

        model.applyRotation(TORSO_BONE, 0f, KnightLibMath.angleDelta(entity.clientLegsYaw, entity.clientTorsoYaw), 0f);
    }

    protected void updatedPlayerControlledBoneYaw(T mech, float partialTick) {
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