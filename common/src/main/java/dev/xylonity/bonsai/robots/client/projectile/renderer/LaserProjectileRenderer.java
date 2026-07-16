package dev.xylonity.bonsai.robots.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.bonsai.robots.client.projectile.model.LaserProjectileModel;
import dev.xylonity.bonsai.robots.common.entity.projectile.LaserProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LaserProjectileRenderer<T extends LaserProjectileEntity> extends GeoEntityRenderer<T> {

    public LaserProjectileRenderer(EntityRendererProvider.Context renderManager) {
        this(renderManager, new LaserProjectileModel<>("laser"));
    }

    public LaserProjectileRenderer(EntityRendererProvider.Context renderManager, LaserProjectileModel<T> model) {
        super(renderManager, model);
        this.shadowRadius = 0.0F;
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        final float yaw = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot());

        super.applyRotations(animatable, poseStack, ageInTicks, yaw, partialTick);

        final float pitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
    }

}
