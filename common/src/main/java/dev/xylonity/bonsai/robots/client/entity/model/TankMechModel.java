package dev.xylonity.bonsai.robots.client.entity.model;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TankMechModel extends GeoModel<TankMechEntity> {

    @Override
    public ResourceLocation getModelResource(TankMechEntity animatable) {
        return Robots.of("geo/tank_mech.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TankMechEntity animatable) {
        return Robots.of("textures/entity/tank_mech_green.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TankMechEntity animatable) {
        return Robots.of("animations/tank_mech.animation.json");
    }

}
