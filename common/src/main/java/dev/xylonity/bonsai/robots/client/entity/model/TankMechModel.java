package dev.xylonity.bonsai.robots.client.entity.model;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import net.minecraft.resources.ResourceLocation;

public class TankMechModel extends AbstractMechModel<TankMechEntity> {

    @Override
    public ResourceLocation getModelResource(TankMechEntity animatable) {
        return Robots.of("geo/tank_mech.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TankMechEntity animatable) {
        return Robots.of("textures/entity/tank_mech/tank_mech_green.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TankMechEntity animatable) {
        return Robots.of("animations/tank_mech.animation.json");
    }

}