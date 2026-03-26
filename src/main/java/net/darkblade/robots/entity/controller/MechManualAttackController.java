package net.darkblade.robots.entity.controller;

import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MechManualAttackController {

    private final TankMechEntity mech;
    private int attackTicks = 0;

    private final int durationTicks = 18;
    private final int[] damageFrames = {4, 5, 6};

    private final double attackRange = 3.0;
    private final double attackWidth = 2.0;
    private final double attackHeight = 3.5;
    private final double zOffset = 2.0;

    public MechManualAttackController(TankMechEntity mech) {
        this.mech = mech;
    }

    public void trigger() {
        if (this.attackTicks == 0) {
            this.attackTicks = 1;
            this.mech.setAttacking(true);
        }
    }

    public void tick() {
        if (this.attackTicks > 0) {
            this.attackTicks++;

            for (int frame : damageFrames) {
                if (this.attackTicks == frame) {
                    applyDamageAndKnockback();
                    break;
                }
            }

            if (this.attackTicks >= durationTicks) {
                this.attackTicks = 0;
                this.mech.setAttacking(false);
            }
        }
    }

    private void applyDamageAndKnockback() {
        List<LivingEntity> entities = this.mech.level().getEntitiesOfClass(
                LivingEntity.class,
                this.mech.getBoundingBox().inflate(attackRange + 3.0, attackHeight + 1.0, attackRange + 3.0),
                e -> e != this.mech && e.isAlive() && !this.mech.hasPassenger(e)
        );

        Vec3 forward = Vec3.directionFromRotation(0, this.mech.yBodyRot);
        Vec3 right = new Vec3(-forward.z, 0, forward.x);
        Vec3 pos = this.mech.position().add(forward.scale(zOffset));

        double boxBottom = this.mech.getY();
        double boxTop = boxBottom + attackHeight;

        for (LivingEntity victim : entities) {
            Vec3 toTarget = victim.position().subtract(pos);

            double forwardDist = forward.dot(toTarget);
            double rightDist = right.dot(toTarget);

            boolean inDepth = forwardDist >= -0.5 && forwardDist <= attackRange;
            boolean inWidth = Math.abs(rightDist) <= attackWidth;
            boolean inHeight = victim.getY() <= boxTop && (victim.getY() + victim.getBbHeight()) >= boxBottom;

            if (inDepth && inWidth && inHeight) {

                double originalYVelocity = victim.getDeltaMovement().y;

                float damage = (float) this.mech.getAttributeValue(Attributes.ATTACK_DAMAGE);
                boolean wasHurt = victim.hurt(this.mech.damageSources().mobAttack(this.mech), damage);

                if (wasHurt) {
                    Vec3 knockbackDir = victim.position().subtract(this.mech.position()).normalize();
                    double knockbackStrength = 2.0D;

                    victim.knockback(knockbackStrength, -knockbackDir.x, -knockbackDir.z);

                    victim.setDeltaMovement(victim.getDeltaMovement().x, originalYVelocity, victim.getDeltaMovement().z);
                    victim.hasImpulse = true;
                }
            }
        }
    }
}