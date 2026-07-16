package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityRegistry;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import dev.xylonity.bonsai.robots.common.entity.ability.tank.*;
import net.minecraft.resources.ResourceLocation;

public final class RobotsAbilities {

    public static final ResourceLocation LAZER_BIG = register("lazer_big", new LazerBigAbility());
    public static final ResourceLocation ROCKET = register("rocket", new RocketAbility());
    public static final ResourceLocation ELECTRIC_FIELD = register("electric_field", new ElectricFieldAbility());
    public static final ResourceLocation HIT = register("hit", new HitAbility());
    public static final ResourceLocation LAZER_SMALL = register("lazer_small", new LazerSmallAbility());

    private static ResourceLocation register(String name, MechAbility ability) {
        final ResourceLocation id = Robots.of(name);
        AbilityRegistry.register(id, ability);
        return id;
    }

    public static void init() {
        ;;
    }

}