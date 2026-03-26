package net.darkblade.robots.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class BaseRobotRenderer<T extends LivingEntity & GeoAnimatable> extends GeoEntityRenderer<T> {

    public BaseRobotRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0.0F;
    }

    @Override
    public int getPackedOverlay(T animatable, float u, float partialTick) {
        if (animatable.deathTime > 0) {
            return OverlayTexture.NO_OVERLAY;
        }

        return super.getPackedOverlay(animatable, u, partialTick);
    }
}