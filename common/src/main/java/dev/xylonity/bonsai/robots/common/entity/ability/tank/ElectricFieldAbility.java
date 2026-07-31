package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import net.minecraft.world.entity.player.Player;

public class ElectricFieldAbility implements MechAbility {

    @Override
    public int cooldownTicks() {
        return 300;
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
