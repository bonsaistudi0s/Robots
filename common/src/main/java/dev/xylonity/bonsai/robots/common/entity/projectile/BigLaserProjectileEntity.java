package dev.xylonity.bonsai.robots.common.entity.projectile;

import dev.xylonity.bonsai.robots.client.particle.RobotsParticles;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.bonsai.robots.registry.RobotsEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BigLaserProjectileEntity extends LaserProjectileEntity {

    public BigLaserProjectileEntity(EntityType<? extends BigLaserProjectileEntity> type, Level level) {
        super(type, level);
    }

    public BigLaserProjectileEntity(Level level, LivingEntity owner) {
        super(RobotsEntities.BIG_LASER_PROJECTILE.get(), owner, level);
    }

    @Override
    protected float getBaseDamage() {
        return (float) Math.max(0.0D, RobotsConfig.BIG_LASER_DAMAGE);
    }

    @Override
    protected int getMinimumFireSeconds() {
        return RobotsConfig.BIG_LASER_MIN_FIRE_SECONDS;
    }

    @Override
    protected int getMaximumFireSeconds() {
        return RobotsConfig.BIG_LASER_MAX_FIRE_SECONDS;
    }

    @Override
    protected double getExplosionRadius() {
        return 3.6D;
    }

    @Override
    protected void spawnTrailParticles() {
        RobotsParticles.bigLaserTrail(this);
    }

    @Override
    protected void spawnImpactParticles() {
        RobotsParticles.bigLaserImpact(this);
    }
}
