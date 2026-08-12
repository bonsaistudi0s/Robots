package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class RobotsDamageTypes {

    public static final ResourceKey<DamageType> ROCKET = ResourceKey.create(Registries.DAMAGE_TYPE, Robots.of("rocket"));

}