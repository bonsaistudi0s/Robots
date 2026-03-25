package net.darkblade.robots.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Renders the player on the "player" bone of the mech.
 * Uses the same approach as SMOP's NirasRiderLayer: navigate the bone hierarchy
 * with translateAndRotate to reach the seat bone, so the player follows animations
 * and stays fixed (no circular movement).
 */
public class MechRiderLayer extends GeoRenderLayer<TankMechEntity> {

    public static final Set<UUID> BLOCKED_RENDERS = new HashSet<>();

    // Fine-tune offsets applied at the end (in the bone's local space)
    private static final float SEAT_OFFSET_X = 0.0f;
    private static final float SEAT_OFFSET_Y = 3.0f;
    private static final float SEAT_OFFSET_Z = 0.0f;

    // Bone chain from root to seat: main -> body -> body_2 -> player
    private static final String[] BONE_CHAIN = {"main", "body", "body_2", "player"};

    public MechRiderLayer(GeoRenderer<TankMechEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, TankMechEntity animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {

        if (!animatable.isVehicle()) return;

        for (Entity passenger : animatable.getPassengers()) {
            if (passenger == Minecraft.getInstance().player
                    && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                continue;
            }

            BLOCKED_RENDERS.remove(passenger.getUUID());

            poseStack.pushPose();

            // === Step 1: Re-enter GeckoLib's model-space ===
            // The layer's poseStack is OUTSIDE actuallyRender's push/pop.
            // We need to re-apply the same transforms that actuallyRender does
            // before rendering bones, so that bone positions are correct.
            // (Same as LivingEntityRenderer: setupRotations + scale + translate)
            float bodyYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
            poseStack.translate(0.0F, 0.01F, 0.0F);

            // === Step 2: Navigate the bone chain (like SMOP's translateAndRotate) ===
            // Each bone's position/rotation has been updated by GeckoLib's animation processor,
            // so this automatically follows animations (idle breathing, walk, etc.)
            boolean chainOk = true;
            for (String boneName : BONE_CHAIN) {
                GeoBone bone = this.getGeoModel().getBone(boneName).orElse(null);
                if (bone == null) { chainOk = false; break; }
                RenderUtils.prepMatrixForBone(poseStack, bone);
            }

            if (chainOk) {
                // Undo the 180-bodyYaw rotation we applied in Step 1.
                // The PlayerRenderer (via our onModelRotation event) will re-apply
                // 180-mechYaw for the player's orientation.
                poseStack.mulPose(Axis.YP.rotationDegrees(-(180.0F - bodyYaw)));

                poseStack.translate(SEAT_OFFSET_X, SEAT_OFFSET_Y, SEAT_OFFSET_Z);

                renderPassenger(passenger, 0, partialTick, poseStack, bufferSource, packedLight);
            }

            poseStack.popPose();

            BLOCKED_RENDERS.add(passenger.getUUID());
        }
    }

    public static <E extends Entity> void renderPassenger(E entityIn, float yaw, float partialTicks,
                                                          PoseStack matrixStack, MultiBufferSource bufferIn,
                                                          int packedLight) {
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super E> render = manager.getRenderer(entityIn);
        if (render != null) {
            try {
                render.render(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
            } catch (Throwable throwable) {
                CrashReport crash = CrashReport.forThrowable(throwable, "Rendering rider on mech");
                CrashReportCategory cat = crash.addCategory("Entity being rendered");
                entityIn.fillCrashReportCategory(cat);
                throw new ReportedException(crash);
            }
        }
    }
}