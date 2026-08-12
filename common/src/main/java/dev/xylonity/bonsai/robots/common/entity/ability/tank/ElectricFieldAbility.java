package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import net.minecraft.world.entity.player.Player;

public class ElectricFieldAbility implements MechAbility {

    @Override
    public int cooldownTicks() {
        return RobotsConfig.TANK_MECH_ELECTRIC_FIELD_COOLDOWN_TICKS;
    }

    @Override
    public float energyCostPerSecond() {
        return 1.0F;
    }

    @Override
    public boolean isToggle() {
        return true;
    }

    @Override
    public void onActivate(AbstractMechEntity mech, Player pilot) {
        ;;
    }

}
