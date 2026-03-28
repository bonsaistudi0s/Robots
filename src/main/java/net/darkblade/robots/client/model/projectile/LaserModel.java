package net.darkblade.robots.client.model.projectile;

import net.darkblade.robots.Robots;
import net.darkblade.robots.entity.projectile.LaserProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LaserModel extends GeoModel<LaserProjectile> {

    @Override
    public ResourceLocation getModelResource(LaserProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "geo/laser_geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LaserProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "textures/entity/projectile/laser.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LaserProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "animations/rocket_animation.json");
    }
}
