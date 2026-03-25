package net.darkblade.robots.client.model;

import net.darkblade.robots.Robots;
import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TankMechModel extends GeoModel<TankMechEntity> {

    @Override
    public ResourceLocation getModelResource(TankMechEntity animatable) {
        return new ResourceLocation(Robots.MODID, "geo/tank_mech.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TankMechEntity animatable) {
        return new ResourceLocation(Robots.MODID, "textures/entity/tank_mech_green.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TankMechEntity animatable) {
        return new ResourceLocation(Robots.MODID, "animations/tank_mech.animation.json");
    }
}