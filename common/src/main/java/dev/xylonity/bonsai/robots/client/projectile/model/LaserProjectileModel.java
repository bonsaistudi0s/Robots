package dev.xylonity.bonsai.robots.client.projectile.model;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.projectile.LaserProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LaserProjectileModel<T extends LaserProjectileEntity> extends GeoModel<T> {

    private final ResourceLocation model;
    private final ResourceLocation texture;

    public LaserProjectileModel(String name) {
        this.model = Robots.of("geo/" + name + ".geo.json");
        this.texture = Robots.of("textures/entity/projectile/" + name + ".png");
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return Robots.of("animations/laser.animation.json");
    }

}
