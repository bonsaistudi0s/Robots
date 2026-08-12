package dev.xylonity.bonsai.robots.common.entity.projectile;

import dev.xylonity.bonsai.robots.client.particle.RobotsParticles;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.bonsai.robots.registry.RobotsEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SmallLaserProjectileEntity extends LaserProjectileEntity {

    public SmallLaserProjectileEntity(EntityType<? extends SmallLaserProjectileEntity> type, Level level) {
        super(type, level);
    }

    public SmallLaserProjectileEntity(Level level, LivingEntity owner) {
        super(RobotsEntities.SMALL_LASER_PROJECTILE.get(), owner, level);
    }

    @Override
    protected float getBaseDamage() {
        return (float) Math.max(0.0D, RobotsConfig.SMALL_LASER_DAMAGE);
    }

    @Override
    protected int getMinimumFireSeconds() {
        return 0;
    }

    @Override
    protected int getMaximumFireSeconds() {
        return 0;
    }

    @Override
    protected double getExplosionRadius() {
        return 1.5D;
    }

    @Override
    protected void spawnTrailParticles() {
        RobotsParticles.smallLaserTrail(this);
    }

    @Override
    protected void spawnImpactParticles() {
        RobotsParticles.smallLaserImpact(this);
    }

}