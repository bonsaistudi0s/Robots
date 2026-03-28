package net.darkblade.robots.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.darkblade.robots.client.model.projectile.LaserModel;
import net.darkblade.robots.entity.projectile.LaserProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LaserRenderer extends GeoEntityRenderer<LaserProjectile> {

    public LaserRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LaserModel());
    }

    @Override
    public RenderType getRenderType(LaserProjectile animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }

    @Override
    protected void applyRotations(LaserProjectile entity, PoseStack poseStack,
                                  float ageInTicks, float rotationYaw, float partialTick) {
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
    }

    @Override
    public int getPackedOverlay(LaserProjectile entity, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }
}
