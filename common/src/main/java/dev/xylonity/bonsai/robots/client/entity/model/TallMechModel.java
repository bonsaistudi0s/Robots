package dev.xylonity.bonsai.robots.client.entity.model;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.mech.TallMechEntity;
import net.minecraft.resources.ResourceLocation;

public class TallMechModel extends AbstractMechModel<TallMechEntity> {

    @Override
    public ResourceLocation getModelResource(TallMechEntity animatable) {
        return Robots.of("geo/tall_mech.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TallMechEntity animatable) {
        return Robots.of("textures/entity/tall_mech/tall_mech_green.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TallMechEntity animatable) {
        return Robots.of("animations/tall_mech.animation.json");
    }

}
