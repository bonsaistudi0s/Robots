package net.darkblade.robots.client.renderer;

import net.darkblade.robots.client.model.TankMechModel;
import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import net.darkblade.robots.client.renderer.layer.MechRiderLayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

public class TankMechRenderer extends GeoEntityRenderer<TankMechEntity> {

    private Vec3 lastPlayerBoneWorldPos = null;

    public Vec3 getLastPlayerBoneWorldPos() { return lastPlayerBoneWorldPos; }

    public TankMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TankMechModel());
        this.shadowRadius = 1.0f;
        addRenderLayer(new MechRiderLayer(this));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, TankMechEntity animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {

        if ("player".equals(bone.getName())) {
            bone.setTrackingMatrices(true);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        if ("player".equals(bone.getName())) {
            var w = bone.getWorldPosition();
            this.lastPlayerBoneWorldPos = new Vec3(w.x(), w.y(), w.z());
        }
    }
}