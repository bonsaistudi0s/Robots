package net.darkblade.robots.client.model;

import net.darkblade.robots.Robots;
import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.model.GeoModel;

public class TankMechModel extends GeoModel<TankMechEntity> {

    @Override
    public ResourceLocation getModelResource(TankMechEntity animatable) {
        return new ResourceLocation(Robots.MODID, "geo/entity/tank_mech.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TankMechEntity animatable) {
        DyeColor color = animatable.getDyeColor();

        if (color != null) {
            return new ResourceLocation(Robots.MODID, "textures/entity/tank_mech/tank_mech_" + color.getName() + ".png");
        }

        return new ResourceLocation(Robots.MODID, "textures/entity/tank_mech/tank_mech_green.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TankMechEntity animatable) {
        return new ResourceLocation(Robots.MODID, "animations/entity/tank_mech.animation.json");
    }
}