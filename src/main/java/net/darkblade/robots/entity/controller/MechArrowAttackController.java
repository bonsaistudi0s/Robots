package net.darkblade.robots.entity.controller;

import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MechArrowAttackController {

    private final TankMechEntity mech;
    private int stateTicks = 0;

    private Vec3 networkSpawnPos = null;

    private static final int SHOOT_ANIM_TICKS = 4;

    public MechArrowAttackController(TankMechEntity mech) {
        this.mech = mech;
    }

    public void trigger(Vec3 spawnPos) {
        int state = this.mech.getArrowState();

        if (state == 0) {
            this.mech.setArrowState(1);
            this.stateTicks = 0;
            this.networkSpawnPos = spawnPos;
            fireArrow();
        }
    }

    public void tick() {
        int state = this.mech.getArrowState();

        if (state == 1) {
            this.stateTicks++;

            if (this.stateTicks >= SHOOT_ANIM_TICKS) {
                this.mech.setArrowState(0);
                this.stateTicks = 0;
            }
        }
    }

    private void fireArrow() {
        if (this.mech.level().isClientSide) return;

        Vec3 spawnPos;

        if (this.networkSpawnPos != null) {
            spawnPos = this.networkSpawnPos;
        } else {
            Vec3 forward = Vec3.directionFromRotation(0, this.mech.yBodyRot);
            Vec3 right = new Vec3(-forward.z, 0, forward.x);
            double xOffset = -0.8;
            double yOffset = 2.5;
            double zOffset = 1.0;
            spawnPos = this.mech.position()
                    .add(right.scale(xOffset))
                    .add(0, yOffset, 0)
                    .add(forward.scale(zOffset));
        }

        Vec3 shootVector;

        if (this.mech.getControllingPassenger() instanceof Player player) {
            double maxReach = 60.0;
            Vec3 eyePosition = player.getEyePosition();
            Vec3 lookAngle = player.getLookAngle();
            Vec3 endPosition = eyePosition.add(lookAngle.scale(maxReach));

            HitResult hitResult = this.mech.level().clip(
                    new ClipContext(
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

        Arrow arrow = new net.minecraft.world.entity.projectile.Arrow(this.mech.level(), this.mech);

        arrow.setEffectsFromItem(new ItemStack(Items.ARROW));

        arrow.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        arrow.shoot(shootVector.x, shootVector.y, shootVector.z, 3.0F, 1.0F);

        this.mech.level().addFreshEntity(arrow);

        this.mech.playSound(net.minecraft.sounds.SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.mech.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }
}