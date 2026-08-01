package dev.xylonity.bonsai.robots.client.projectile.renderer;

import dev.xylonity.bonsai.robots.common.entity.projectile.RocketProjectileEntity;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;

public class RocketProjectileRenderer extends AbstractProjectileRenderer<RocketProjectileEntity> {

    public RocketProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, "rocket", "rocket_glow");
    }

    @Override
    protected void setupPose(RocketProjectileEntity rocket, KnightLibModel model, float partialTicks) {
        final float pitch = Mth.lerp(partialTicks, rocket.xRotO, rocket.getXRot());
        model.applyRotation("main", pitch, 180.0F, 0.0F);
    }

}
