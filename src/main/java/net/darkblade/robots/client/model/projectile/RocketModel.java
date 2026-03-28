package net.darkblade.robots.client.model.projectile;

import net.darkblade.robots.Robots;
import net.darkblade.robots.entity.projectile.RocketProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RocketModel extends GeoModel<RocketProjectile> {

    @Override
    public ResourceLocation getModelResource(RocketProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "geo/rocket_geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RocketProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "textures/entity/projectile/rocket.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RocketProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "animations/rocket_animation.json");
    }
}
