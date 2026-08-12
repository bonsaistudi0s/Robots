package dev.xylonity.bonsai.robots.common.entity.projectile;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.knightlib.api.animation.KnightLibAnimatable;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class LaserProjectileEntity extends RobotProjectileEntity implements KnightLibAnimatable {

    private final KnightLibAnimationHandler animations = KnightLibAnimationHandler.of(this);

    private static final EntityDataAccessor<Boolean> DATA_ELECTRIC = SynchedEntityData.defineId(LaserProjectileEntity.class, EntityDataSerializers.BOOLEAN);

    private final Set<UUID> piercedEntities = new HashSet<>();

    private static final byte IMPACT_EVENT_ID = 64;

    public LaserProjectileEntity(EntityType<? extends LaserProjectileEntity> type, Level level) {
        super(type, level);
    }

    protected LaserProjectileEntity(EntityType<? extends LaserProjectileEntity> type, LivingEntity owner, Level level) {
        super(type, owner, level);
        this.setElectric(owner instanceof AbstractMechEntity mech && mech.isElectricMode());
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount >= 100) {
            this.discard();
        }
        else if (this.level().isClientSide && this.isAlive()) {
            this.spawnTrailParticles();
        }

    }

    protected float getDamage() {
        final float multiplier = this.isElectric() ? (float) Math.max(0.0D, RobotsConfig.ELECTRIC_LASER_DAMAGE_MULTIPLIER) : 1.0F;
        return getBaseDamage() * multiplier;
    }

    protected abstract float getBaseDamage();

    protected abstract int getMinimumFireSeconds();

    protected abstract int getMaximumFireSeconds();

    protected abstract double getExplosionRadius();

    protected float getExplosionDamageScale() {
        return 0.25F;
    }

    protected boolean piercesEntities() {
        return false;
    }

    protected abstract void spawnTrailParticles();

    protected abstract void spawnImpactParticles();

    public boolean isElectric() {
        return this.entityData.get(DATA_ELECTRIC);
    }

    public void setElectric(boolean electric) {
        this.entityData.set(DATA_ELECTRIC, electric);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ELECTRIC, false);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Electric", this.isElectric());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setElectric(tag.getBoolean("Electric"));
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !this.piercedEntities.contains(target.getUUID());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.piercesEntities()) {
            this.piercedEntities.add(result.getEntity().getUUID());
        }

        if (!this.level().isClientSide) {
            final Entity owner = this.getOwner();
            if (result.getEntity().hurt(this.damageSources().thrown(this, owner), this.getDamage())) {
                this.ignite(result.getEntity());
            }

        }

    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, IMPACT_EVENT_ID);
            this.damageNearbyEntities(result);
        }

        super.onHit(result);
        if (!this.level().isClientSide && (!(result instanceof EntityHitResult) || !this.piercesEntities())) {
            this.discard();
        }

    }

    private void damageNearbyEntities(HitResult result) {
        final double radius = Math.max(0.0D, this.getExplosionRadius());
        if (radius <= 0.0D) {
            return;
        }

        final Entity owner = this.getOwner();
        final Entity directEntity = result instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
        final Vec3 impactPosition = result.getLocation();
        final float maximumSplashDamage = this.getDamage() * Math.max(0.0F, this.getExplosionDamageScale());
        for (final LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(impactPosition, radius * 2.0D, radius * 2.0D, radius * 2.0D))) {
            if (target == directEntity || target == owner) {
                continue;
            }
            if (owner != null && target.isPassengerOfSameVehicle(owner)) {
                continue;
            }

            final double distance = Math.sqrt(target.distanceToSqr(impactPosition));
            if (distance >= radius) {
                continue;
            }

            final float damage = maximumSplashDamage * (1.0F - (float) (distance / radius));
            if (damage > 0.0F) {
                target.hurt(this.damageSources().thrown(this, owner), damage);
            }

        }

    }

    private void ignite(Entity target) {
        final int configuredMaximum = Math.max(0, this.getMaximumFireSeconds());
        if (configuredMaximum == 0) {
            return;
        }

        final int configuredMinimum = Math.max(0, this.getMinimumFireSeconds());
        final int minimum = Math.min(configuredMinimum, configuredMaximum);
        final int maximum = Math.max(configuredMinimum, configuredMaximum);
        final int duration = minimum + (maximum > minimum ? this.random.nextInt(maximum - minimum + 1) : 0);
        if (duration > 0) {
            target.setSecondsOnFire(duration);
        }

    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == IMPACT_EVENT_ID) {
            this.spawnImpactParticles();
        }
        else {
            super.handleEntityEvent(id);
        }

    }

    @Override
    public KnightLibAnimationHandler getAnimationHandler() {
        return this.animations;
    }

}
