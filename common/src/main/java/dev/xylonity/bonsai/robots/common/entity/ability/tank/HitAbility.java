package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import net.minecraft.world.entity.player.Player;

public class HitAbility implements MechAbility {

    @Override
    public int cooldownTicks() {
        return 20;
    }

    @Override
    public void onActivate(AbstractMechEntity mech, Player pilot) {
        if (mech instanceof TankMechEntity tankMech) {
            tankMech.playAttackAnimation();
        }

    }

}
