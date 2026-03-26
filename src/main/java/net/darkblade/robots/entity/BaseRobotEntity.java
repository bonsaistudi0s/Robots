package net.darkblade.robots.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class BaseRobotEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Integer> DATA_DYE_COLOR = SynchedEntityData.defineId(BaseRobotEntity.class, EntityDataSerializers.INT);

    protected BaseRobotEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DYE_COLOR, -1);
    }

    public DyeColor getDyeColor() {
        int colorId = this.entityData.get(DATA_DYE_COLOR);
        if (colorId == -1) return null;
        return DyeColor.byId(colorId);
    }

    public void setDyeColor(net.minecraft.world.item.DyeColor color) {
        this.entityData.set(DATA_DYE_COLOR, color.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getDyeColor() != null) {
            compound.putInt("DyeColor", this.getDyeColor().getId());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("DyeColor")) {
            this.setDyeColor(DyeColor.byId(compound.getInt("DyeColor")));
        }
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