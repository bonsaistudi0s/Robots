package net.darkblade.robots.entity.controller;

import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MechRangedAttackController {

    private final TankMechEntity mech;
    private int stateTicks = 0;

    private static final int AIM_IN_TICKS = 9;
    private static final int SHOOT_TICKS = 5;
    private static final int POST_SHOOT_WAIT = 20;
    private static final int AIM_OFF_TICKS = 9;

    public MechRangedAttackController(TankMechEntity mech) {
        this.mech = mech;
    }

    public void trigger() {
        int state = this.mech.getRangedState();

        if (state == 0) {
            this.mech.setRangedState(1);
            this.stateTicks = 0;
        }
        else if (state == 1 && this.stateTicks >= AIM_IN_TICKS) {
            this.mech.setRangedState(2);
            this.stateTicks = 0;
            fireProjectile();
        }
    }

    public void tick() {
        int state = this.mech.getRangedState();

        if (state != 0) {
            this.stateTicks++;

            if (state == 2 && this.stateTicks >= SHOOT_TICKS) {
                this.mech.setRangedState(3);
                this.stateTicks = 0;
            }
            else if (state == 3 && this.stateTicks >= POST_SHOOT_WAIT) {
                this.mech.setRangedState(4);
                this.stateTicks = 0;
            }
            else if (state == 4 && this.stateTicks >= AIM_OFF_TICKS) {
                this.mech.setRangedState(0);
                this.stateTicks = 0;
            }
        }
    }

    private void fireProjectile() {
        if (this.mech.level().isClientSide) return;

        Vec3 forward = Vec3.directionFromRotation(0, this.mech.yBodyRot);
        Vec3 right = new Vec3(-forward.z, 0, forward.x);

        double xOffset = 1.2;
        double yOffset = 2.0;
        double zOffset = 1.5;

        Vec3 spawnPos = this.mech.position()
                .add(right.scale(xOffset))
                .add(0, yOffset, 0)
                .add(forward.scale(zOffset));

        Vec3 shootVector;

        if (this.mech.getControllingPassenger() instanceof Player player) {

            double maxReach = 60.0;
            Vec3 eyePosition = player.getEyePosition();
            Vec3 lookAngle = player.getLookAngle();
            Vec3 endPosition = eyePosition.add(lookAngle.scale(maxReach));

            HitResult hitResult = this.mech.level().clip(
                    new net.minecraft.world.level.ClipContext(
                            eyePosition,
                            endPosition,
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            player
                    )
            );

            Vec3 targetPoint = hitResult.getLocation();

            shootVector = targetPoint.subtract(spawnPos).normalize();

        } else {
            shootVector = this.mech.getLookAngle();
        }

        SmallFireball fireball = new SmallFireball(
                this.mech.level(), this.mech, shootVector.x, shootVector.y, shootVector.z);

        fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        this.mech.level().addFreshEntity(fireball);

        this.mech.playSound(SoundEvents.BLAZE_SHOOT, 1.5F, 1.0F);
    }
}