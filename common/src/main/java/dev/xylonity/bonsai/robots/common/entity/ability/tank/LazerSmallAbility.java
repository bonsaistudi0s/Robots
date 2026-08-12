package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import dev.xylonity.bonsai.robots.common.entity.projectile.SmallLaserProjectileEntity;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import net.minecraft.world.entity.player.Player;

public class LazerSmallAbility implements MechAbility {

    @Override
    public int cooldownTicks() {
        return RobotsConfig.TANK_MECH_SMALL_LASER_COOLDOWN_TICKS;
    }

    @Override
    public void onActivate(AbstractMechEntity mech, Player pilot) {
        LaserAbilityHelper.fire(mech, pilot, new SmallLaserProjectileEntity(mech.level(), mech), 0.0D);
    }

}
