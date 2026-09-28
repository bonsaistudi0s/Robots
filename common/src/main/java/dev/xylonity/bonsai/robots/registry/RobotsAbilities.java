package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityRegistry;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import dev.xylonity.bonsai.robots.common.entity.ability.tank.*;
import dev.xylonity.bonsai.robots.common.entity.ability.shared.ElectricFieldAbility;
import dev.xylonity.bonsai.robots.common.entity.ability.shield.ShieldSphereAbility;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import net.minecraft.resources.ResourceLocation;

public final class RobotsAbilities {

    public static final ResourceLocation LAZER_BIG = register("lazer_big", new LazerBigAbility());
    public static final ResourceLocation ROCKET = register("rocket", new RocketAbility());
    public static final ResourceLocation ELECTRIC_FIELD = register("electric_field", new ElectricFieldAbility(
            () -> RobotsConfig.ELECTRIC_FIELD_COOLDOWN_TICKS, () -> RobotsConfig.ELECTRIC_FIELD_ENERGY_PER_SECOND));
    public static final ResourceLocation SHIELD_SPHERE = register("shield_sphere", new ShieldSphereAbility());
    public static final ResourceLocation HIT = register("hit", new HitAbility(20, 5, 3.5D));
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
