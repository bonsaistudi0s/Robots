package net.darkblade.robots.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MechRiderLayer extends GeoRenderLayer<TankMechEntity> {

    public static final Set<UUID> BLOCKED_RENDERS = new HashSet<>();

    private static final float SEAT_OFFSET_X = 0.0f;
    private static final float SEAT_OFFSET_Y = -0.6f;
    private static final float SEAT_OFFSET_Z = -0.1f;

    private static final float BONE_BASE_MODEL_Y = -(57.7f / 16.0f - 1.501f);

    public MechRiderLayer(GeoRenderer<TankMechEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, TankMechEntity animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {

        if (!animatable.isVehicle()) return;

        GeoBone seatBone = this.getGeoModel().getBone("player").orElse(null);
        if (seatBone == null) return;

        Matrix4f modelMat = seatBone.getModelSpaceMatrix();

        float animDeltaY = 0;
        if (modelMat != null) {
            Vector4f boneModelPos = modelMat.transform(new Vector4f(0, 0, 0, 1));
            animDeltaY = -(boneModelPos.y() - BONE_BASE_MODEL_Y);
        }

        float finalY = (57.7f / 16.0f - 1.501f) + animDeltaY + SEAT_OFFSET_Y;

        for (Entity passenger : animatable.getPassengers()) {
            if (passenger == Minecraft.getInstance().player
                    && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                continue;
            }

            BLOCKED_RENDERS.remove(passenger.getUUID());

            poseStack.pushPose();
            poseStack.translate(
                    SEAT_OFFSET_X,
                    finalY,
                    SEAT_OFFSET_Z
            );

            renderPassenger(passenger, 0, partialTick, poseStack, bufferSource, packedLight);

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