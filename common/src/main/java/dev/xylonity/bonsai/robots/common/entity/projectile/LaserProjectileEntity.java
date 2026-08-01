package dev.xylonity.bonsai.robots.common.entity.projectile;

import dev.xylonity.bonsai.robots.client.particle.RobotsParticles;
import dev.xylonity.bonsai.robots.registry.RobotsEntities;
import dev.xylonity.knightlib.api.animation.KnightLibAnimatable;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LaserProjectileEntity extends ThrowableProjectile implements KnightLibAnimatable {

    private final KnightLibAnimationHandler animations = KnightLibAnimationHandler.of(this);

    public LaserProjectileEntity(EntityType<? extends LaserProjectileEntity> type, Level level) {
        super(type, level);
    }

    public LaserProjectileEntity(Level level, LivingEntity owner) {
        super(RobotsEntities.LASER_PROJECTILE.get(), owner, level);
    }

    protected LaserProjectileEntity(EntityType<? extends LaserProjectileEntity> type, LivingEntity owner, Level level) {
        super(type, owner, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount >= 100) {
            this.discard();
        }

    }

    protected float getDamage() {
        return 20.0F;
    }

    @Override
    protected void defineSynchedData() {
        ;;
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            final Entity owner = this.getOwner();
            result.getEntity().hurt(this.damageSources().thrown(this, owner), this.getDamage());
        }

    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }

    }

    @Override
    public KnightLibAnimationHandler getAnimationHandler() {
        return this.animations;
    }

}
