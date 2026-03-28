package net.darkblade.robots.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.darkblade.robots.Robots;
import net.darkblade.robots.client.model.projectile.RocketModel;
import net.darkblade.robots.entity.projectile.RocketProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class RocketRenderer extends GeoEntityRenderer<RocketProjectile> {

    private static final ResourceLocation GLOW_TEXTURE =
            new ResourceLocation(Robots.MODID, "textures/entity/projectile/rocket_glow.png");

    public RocketRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RocketModel());
        addRenderLayer(new GlowLayer(this));
    }

    @Override
    protected void applyRotations(RocketProjectile entity, PoseStack poseStack,
                                  float ageInTicks, float rotationYaw, float partialTick) {
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
    }

    @Override
    public int getPackedOverlay(RocketProjectile entity, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    private static class GlowLayer extends GeoRenderLayer<RocketProjectile> {
        public GlowLayer(GeoEntityRenderer<RocketProjectile> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, RocketProjectile entity, BakedGeoModel model,
                           RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                           float partialTick, int packedLight, int packedOverlay) {
            RenderType glowType = RenderType.entityTranslucentEmissive(GLOW_TEXTURE);
            VertexConsumer glowBuffer = bufferSource.getBuffer(glowType);
            this.getRenderer().reRender(model, poseStack, bufferSource, entity, glowType,
                    glowBuffer, partialTick, 15728880, OverlayTexture.NO_OVERLAY,
                    1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
