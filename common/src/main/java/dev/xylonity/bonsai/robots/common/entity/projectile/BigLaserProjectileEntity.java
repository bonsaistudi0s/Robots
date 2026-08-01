package dev.xylonity.bonsai.robots.common.entity.projectile;

import dev.xylonity.bonsai.robots.client.particle.RobotsParticles;
import dev.xylonity.bonsai.robots.registry.RobotsEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BigLaserProjectileEntity extends LaserProjectileEntity {

    private static final byte IMPACT_EVENT_ID = 64;

    public BigLaserProjectileEntity(EntityType<? extends BigLaserProjectileEntity> type, Level level) {
        super(type, level);
    }

    public BigLaserProjectileEntity(Level level, LivingEntity owner) {
        super(RobotsEntities.BIG_LASER_PROJECTILE.get(), owner, level);
    }

    @Override
    protected float getDamage() {
        return 40.0F;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            result.getEntity().setSecondsOnFire(4);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {

            // So the hit call reaches the tracking entities (vanilla sometimes fails and doesn't call this method on hit anyway lol)
            this.level().broadcastEntityEvent(this, IMPACT_EVENT_ID);

            final Entity owner = this.getOwner();
            final Entity directEntity = result instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
            final Vec3 impactPosition = result.getLocation();
            for (final LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(impactPosition, 6, 6, 6))) {
                if (target == directEntity || target == owner) {
                    continue;
                }
                if (owner != null && target.isPassengerOfSameVehicle(owner)) {
                    continue;
                }

                double distance = Math.sqrt(target.distanceToSqr(impactPosition));
                if (distance > 3) {
                    continue;
                }

                target.hurt(this.damageSources().thrown(this, owner), 10f * (1.0F - (float) (distance / 3)));
            }

        }

        super.onHit(result);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == IMPACT_EVENT_ID) {
            // laser particles go brr
        }
        else {
            super.handleEntityEvent(id);
        }

    }

}