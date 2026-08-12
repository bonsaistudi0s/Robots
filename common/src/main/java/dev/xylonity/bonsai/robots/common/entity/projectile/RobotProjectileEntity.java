package dev.xylonity.bonsai.robots.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public abstract class RobotProjectileEntity extends ThrowableProjectile {

    protected RobotProjectileEntity(EntityType<? extends RobotProjectileEntity> type, Level level) {
        super(type, level);
    }

    protected RobotProjectileEntity(EntityType<? extends RobotProjectileEntity> type, LivingEntity owner, Level level) {
        super(type, owner, level);
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

}