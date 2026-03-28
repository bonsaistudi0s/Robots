package net.darkblade.robots.client.model.projectile;

import net.darkblade.robots.Robots;
import net.darkblade.robots.entity.projectile.BigLaserProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BigLaserModel extends GeoModel<BigLaserProjectile> {

    @Override
    public ResourceLocation getModelResource(BigLaserProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "geo/big_laser.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BigLaserProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "textures/entity/projectile/big_laser.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BigLaserProjectile animatable) {
        return new ResourceLocation(Robots.MODID, "animations/rocket_animation.json");
    }
}
