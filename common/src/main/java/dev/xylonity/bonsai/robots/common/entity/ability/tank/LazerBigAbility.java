package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import dev.xylonity.bonsai.robots.common.entity.projectile.BigLaserProjectileEntity;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import net.minecraft.world.entity.player.Player;

public class LazerBigAbility implements MechAbility {

    @Override
    public int cooldownTicks() {
        return RobotsConfig.TANK_MECH_BIG_LASER_COOLDOWN_TICKS;
    }

    @Override
    public int aimTicks() {
        return 9;
    }

    @Override
    public boolean autoAimOnSelect() {
        return true;
    }

    @Override
    public int aimReleaseDelayTicks() {
        return 5;
    }

    @Override
    public void onActivate(AbstractMechEntity mech, Player pilot) {
        LaserAbilityHelper.fire(mech, pilot, new BigLaserProjectileEntity(mech.level(), mech), 1.0D);
    }

}