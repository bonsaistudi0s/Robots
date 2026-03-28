package net.darkblade.robots.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.darkblade.robots.client.model.projectile.BigLaserModel;
import net.darkblade.robots.entity.projectile.BigLaserProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BigLaserRenderer extends GeoEntityRenderer<BigLaserProjectile> {

    public BigLaserRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BigLaserModel());
    }

    @Override
    public RenderType getRenderType(BigLaserProjectile animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }

    @Override
    protected void applyRotations(BigLaserProjectile entity, PoseStack poseStack,
                                  float ageInTicks, float rotationYaw, float partialTick) {
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
    }

    @Override
    public int getPackedOverlay(BigLaserProjectile entity, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }
}
