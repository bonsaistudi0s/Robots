package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import net.minecraft.world.entity.player.Player;

public class LazerSmallAbility implements MechAbility {

    @Override
    public int cooldownTicks() {
        return 30;
    }

    @Override
    public void onActivate(AbstractMechEntity mech, Player pilot) {
        ;;
    }

}
