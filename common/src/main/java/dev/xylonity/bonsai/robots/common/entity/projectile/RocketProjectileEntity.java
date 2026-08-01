package dev.xylonity.bonsai.robots.common.entity.projectile;

import dev.xylonity.bonsai.robots.registry.RobotsEntities;
import dev.xylonity.knightlib.api.animation.KnightLibAnimatable;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RocketProjectileEntity extends ThrowableProjectile implements KnightLibAnimatable {

    private final KnightLibAnimationHandler animations = KnightLibAnimationHandler.of(this);

    private static final int EJECTION_TICKS = 8;

    private int targetId;
    private Vec3 fixedTarget;
    private boolean exploded;
    private int directHitId = -1;

    public RocketProjectileEntity(EntityType<? extends RocketProjectileEntity> type, Level level) {
        super(type, level);
    }

    public RocketProjectileEntity(Level level, LivingEntity owner, @Nullable LivingEntity target, Vec3 targetPosition) {
        super(RobotsEntities.ROCKET_PROJECTILE.get(), owner, level);
        this.targetId = target == null ? 0 : target.getId();
        this.fixedTarget = targetPosition;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.tickCount >= EJECTION_TICKS) {
            moveToTarget();
        }

        super.tick();

        ProjectileUtil.rotateTowardsMovement(this, 1.0F);

        if (this.level().isClientSide) {
            spawnTrailParticles();
        }

        if (!this.level().isClientSide && this.tickCount >= 160) {
            explode(this.position());
        }

    }

    private void spawnTrailParticles() {
        final Vec3 speed = this.getDeltaMovement();
        if (speed.lengthSqr() < 1.0E-6D) {
            return;
        }

        final Vec3 normalized = this.position().subtract(speed.normalize().scale(0.35D));
        final double x = normalized.x + this.random.nextGaussian() * 0.035D;
        final double y = normalized.y + this.random.nextGaussian() * 0.035D;
        final double z = normalized.z + this.random.nextGaussian() * 0.035D;
        final Vec3 drift = speed.normalize().scale(-0.025D);
        this.level().addParticle(ParticleTypes.POOF, x, y, z, drift.x, drift.y + 0.008D, drift.z);

        if (this.random.nextFloat() < 0.4F) {
            this.level().addParticle(ParticleTypes.FLAME, x, y, z, drift.x * 0.5D, drift.y * 0.5D, drift.z * 0.5D);
        }

    }

    /// Homes the rocket towards its target with random noise over the straight line direction
    private void moveToTarget() {
        final Vec3 target = targetPosition();
        if (target == null) {
            return;
        }

        final Vec3 offset = target.subtract(this.position());
        final double distance = offset.length();

        // The radius scales with the current speed so a fast rocket shouldn't tunnel past the target between ticks
        final double arrivalRadius = Math.max(0.6D, this.getDeltaMovement().length() * 1.1D);
        if (distance < arrivalRadius) {
            explode(target);
            return;
        }

        // Frame around the rocket for later random deviation
        final Vec3 axis = offset.scale(1.0D / distance);
        final Vec3 reference = Math.abs(axis.y) < 0.9D ? new Vec3(0.0D, 1.0D, 0.0D) : new Vec3(1.0D, 0.0D, 0.0D);
        final Vec3 right = axis.cross(reference).normalize();
        final Vec3 up = right.cross(axis).normalize();

        // Passes the deviation when close to the target position (up to a 7 block distance) so the rocket
        // doesn't tweak around it (specially for entities)
        final double maxDeviation = Math.min(1.0D, distance / 7.0D);

        // Each rocket has its own movement
        final int seed = this.getUUID().hashCode();
        final double phase = unit(seed, 17) * Math.PI * 2.0D;

        // Main spiral movement (position scales per tick)
        final double frequency = 0.22D + unit(seed, 31) * 0.12D;
        final double spiralStrength = (0.42D + unit(seed, 47) * 0.3D) * maxDeviation;
        final double angle = phase + this.tickCount * frequency;
        final Vec3 spiral = right.scale(Math.cos(angle)).add(up.scale(Math.sin(angle))).scale(spiralStrength);

        // Additional slower oscillation
        final double secondaryAngle = phase * 1.7D + this.tickCount * (0.08D + unit(seed, 59) * 0.07D);
        final double swayStrength = (0.14D + unit(seed, 67) * 0.14D) * maxDeviation;
        final Vec3 sway = right.scale(Math.sin(secondaryAngle))
                .add(up.scale(Math.cos(secondaryAngle * 0.83D)))
                .scale(swayStrength);

        // Slows sideways sway on the right axis only (with some deviation to the spiral)
        final double wobble = Math.sin(angle * 0.61D + unit(seed, 73) * Math.PI * 2.0D) * 0.17D * maxDeviation;

        // Random kick that only changes direction every 5 ticks
        final int kickStep = this.tickCount / 5;
        final double kickAngle = unit(seed ^ kickStep * 0x45D9F3B, 89) * Math.PI * 2.0D;
        final double kickStrength = (0.08D + unit(seed, 97) * 0.08D) * maxDeviation;
        final Vec3 kick = right.scale(Math.cos(kickAngle)).add(up.scale(Math.sin(kickAngle))).scale(kickStrength);

        // Straight line direction plus every deviation, normalized again
        Vec3 desiredPosition = axis.add(spiral).add(sway).add(kick).add(right.scale(wobble)).normalize();

        // Smoothing the actual direction with the previous deviation calculations
        final Vec3 currentVelocity = this.getDeltaMovement();
        final Vec3 currentDirection = currentVelocity.lengthSqr() > 1.0E-6D ? currentVelocity.normalize() : desiredPosition;
        final double assist = 1.0D - maxDeviation;
        final double turnRate = (this.tickCount < 18 ? 0.1D : 0.18D) + assist * 0.3D;
        desiredPosition = currentDirection.scale(1.0D - turnRate).add(desiredPosition.scale(turnRate)).normalize();

        // Accelerates over time up to a cap and then fades in and out the current speed to not keep it constant
        final double baseSpeed = Math.min(1.25D, 0.78D + (this.tickCount - EJECTION_TICKS) * 0.013D);
        final double speed = baseSpeed * (0.82D + 0.25D * (0.5D + 0.5D * Math.sin(secondaryAngle * 1.35D)));
        this.setDeltaMovement(desiredPosition.scale(speed));
    }

    @Nullable
    private Vec3 targetPosition() {
        if (this.targetId != 0) {
            final Entity target = this.level().getEntity(this.targetId);
            if (target instanceof LivingEntity living && living.isAlive()) {
                this.fixedTarget = living.getBoundingBox().getCenter();
            }
            else {
                this.targetId = 0;
            }

        }

        return this.fixedTarget;
    }

    /// Aligns the first render rotation before the spawm packet is sent
    public void pointAlongMovement() {
        ProjectileUtil.rotateTowardsMovement(this, 1.0F);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    /// lowbias32 https://github.com/skeeto/hash-prospector
    private static double unit(int seed, int salt) {
        int value = seed ^ salt * 0x9E3779B9;
        value ^= value >>> 16;
        value *= 0x7FEB352D;
        value ^= value >>> 15;
        return (value & 0xFFFF) / 65535.0D;
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
            this.directHitId = result.getEntity().getId();
            final Entity owner = this.getOwner();
            result.getEntity().hurt(this.damageSources().thrown(this, owner), 24f);
        }

    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            explode(result.getLocation());
        }

    }

    private void explode(Vec3 position) {
        if (this.exploded || this.level().isClientSide) {
            return;
        }

        this.exploded = true;
        this.setPos(position);
        damageNearbyEntities(position);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF, position.x, position.y, position.z, 52, 1.0D, 1.0D, 1.0D, 0.18D);
            serverLevel.sendParticles(ParticleTypes.FLAME, position.x, position.y, position.z, 24, 0.8D, 0.8D, 0.8D, 0.13D);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, position.x, position.y, position.z, 32, 1.1D, 1.1D, 1.1D, 0.24D);
            serverLevel.sendParticles(ParticleTypes.SMOKE, position.x, position.y, position.z, 22, 0.75D, 0.75D, 0.75D, 0.1D);
        }

        this.level().playSound(null, position.x, position.y, position.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.2F, 0.82F + this.random.nextFloat() * 0.28F);
        this.discard();
    }

    private void damageNearbyEntities(Vec3 position) {
        final Entity owner = this.getOwner();
        final float radius = 3.5f;
        final AABB bounds = new AABB(position, position).inflate(radius);
        for (
                final LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bounds,
                target -> target.isAlive() && target != owner && target.getId() != this.directHitId)
        ) {
            final double distance = position.distanceTo(entity.getBoundingBox().getCenter());
            if (distance >= radius) {
                continue;
            }

            final float damage = 24f * (float) (1.0D - distance / radius);
            if (damage > 1.0F) {
                entity.hurt(this.damageSources().thrown(this, owner), damage);
            }

        }

    }

    @Override
    public KnightLibAnimationHandler getAnimationHandler() {
        return this.animations;
    }

}