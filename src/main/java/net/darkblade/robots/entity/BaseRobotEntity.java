package net.darkblade.robots.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class BaseRobotEntity extends PathfinderMob implements GeoEntity {

    protected BaseRobotEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void die(DamageSource cause) {
        this.ejectPassengers();
        super.die(cause);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime >= 50) {
            this.remove(Entity.RemovalReason.KILLED);
        }

        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        this.setYRot(this.yRotO);
        this.setYBodyRot(this.yBodyRotO);
        this.setXRot(this.xRotO);
    }

    public boolean isDying() {
        return this.deathTime > 0 || this.isDeadOrDying();
    }
}