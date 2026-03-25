package net.darkblade.robots.entity.ai;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class MechMeleeAttackGoal<E extends PathfinderMob> extends Goal {

    public static boolean DEBUG = false;

    public interface AttackAnimBridge { void setAttacking(boolean value); }

    private final E mob;
    private final double attackRange;
    private final double attackWidth;
    private final double attackHeight;

    private final double xOffset;
    private final double yOffset;
    private final double zOffset;

    private final double triggerDistance;

    private final double speedModifier;
    private final int durationTicks;
    private final int[] damageFrames;
    private final int cooldownBase;
    private final AttackAnimBridge animBridge;

    private boolean active = false;
    private int tick = 0;
    private int cooldown = 0;
    private int ticksUntilNextPathRecalculation = 0;

    public MechMeleeAttackGoal(E mob, double attackRange, double attackWidth, double attackHeight, double xOffset, double yOffset, double zOffset, double triggerDistance, double speedModifier, int durationTicks, int[] damageFrames, int cooldownBase, AttackAnimBridge animBridge) {
        this.mob = mob;
        this.attackRange = attackRange;
        this.attackWidth = attackWidth;
        this.attackHeight = attackHeight;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.triggerDistance = triggerDistance;
        this.speedModifier = speedModifier;
        this.durationTicks = durationTicks;
        this.damageFrames = damageFrames;
        this.cooldownBase = cooldownBase;
        this.animBridge = animBridge;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return active || (mob.getTarget() != null && mob.getTarget().isAlive());
    }

    @Override
    public void start() {
        this.ticksUntilNextPathRecalculation = 0;
    }

    @Override
    public void stop() {
        setAttackActive(false);
        this.tick = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();

        if (target == null || !target.isAlive()) {
            if (active) updateAttackFrames();
            return;
        }

        if (!active && this.cooldown > 0) {
            this.cooldown--;
        }

        if (!active) {
            mob.getLookControl().setLookAt(target, 60.0F, 40.0F);

            double distSq = mob.distanceToSqr(target.getX(), mob.getY(), target.getZ());
            double triggerSq = (mob.getBbWidth() / 2.0 + target.getBbWidth() / 2.0 + this.triggerDistance);
            triggerSq *= triggerSq;

            double directMoveThreshold = 8.0 * 8.0;
            if (distSq <= directMoveThreshold) {
                mob.getNavigation().stop();
                Vec3 dir = target.position().subtract(mob.position()).normalize();
                double speed = mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) * this.speedModifier;
                mob.setDeltaMovement(dir.x * speed, mob.getDeltaMovement().y, dir.z * speed);
            } else if (--this.ticksUntilNextPathRecalculation <= 0) {
                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(10);
                mob.getNavigation().moveTo(target, this.speedModifier);
            }

            if (distSq <= triggerSq && mob.getSensing().hasLineOfSight(target) && this.cooldown <= 0) {
                startAttack();
            }
        } else {
            updateAttackFrames();
        }
    }

    private void startAttack() {
        this.tick = 0;
        mob.getNavigation().stop();
        setAttackActive(true);
    }

    private void setAttackActive(boolean v) {
        if (this.active == v) return;
        this.active = v;
        this.animBridge.setAttacking(v);
    }

    private void updateAttackFrames() {
        this.tick++;

        for (int frame : this.damageFrames) {
            if (this.tick == frame) {
                applyDamageWithVectorMath();

                if (DEBUG && !this.mob.level().isClientSide()) {
                    drawDebugBox();
                }
                break;
            }
        }

        if (this.tick >= this.durationTicks) {
            setAttackActive(false);
            this.cooldown = this.cooldownBase;
        }
    }

    private void applyDamageWithVectorMath() {
        double maxXZOffset = Math.max(Math.abs(this.xOffset), Math.abs(this.zOffset));
        List<LivingEntity> entities = mob.level().getEntitiesOfClass(
                LivingEntity.class,
                mob.getBoundingBox().inflate(this.attackRange + maxXZOffset + 1.0, this.attackHeight + Math.abs(this.yOffset) + 1.0, this.attackRange + maxXZOffset + 1.0),
                e -> e != mob && e.isAlive() && mob.canAttack(e)
        );

        Vec3 forward = Vec3.directionFromRotation(0, mob.yBodyRot);
        Vec3 right = new Vec3(-forward.z, 0, forward.x);

        Vec3 pos = mob.position()
                .add(0, this.yOffset, 0)
                .add(forward.scale(this.zOffset))
                .add(right.scale(this.xOffset));

        double boxBottom = mob.getY() + this.yOffset;
        double boxTop = boxBottom + this.attackHeight;

        for (LivingEntity victim : entities) {
            Vec3 toTarget = victim.position().subtract(pos);

            double forwardDist = forward.dot(toTarget);
            double rightDist = right.dot(toTarget);

            boolean inDepth = forwardDist >= -0.5 && forwardDist <= this.attackRange;
            boolean inWidth = Math.abs(rightDist) <= this.attackWidth;
            boolean inHeight = victim.getY() <= boxTop && (victim.getY() + victim.getBbHeight()) >= boxBottom;

            if (inDepth && inWidth && inHeight) {
                mob.doHurtTarget(victim);
            }
        }
    }

    private void drawDebugBox() {
        if (!(mob.level() instanceof ServerLevel level)) return;

        Vec3 forward = Vec3.directionFromRotation(0, mob.yBodyRot);
        Vec3 right = new Vec3(-forward.z, 0, forward.x);

        Vec3 pos = mob.position()
                .add(0, this.yOffset, 0)
                .add(forward.scale(this.zOffset))
                .add(right.scale(this.xOffset));

        Vec3 b1 = pos.add(forward.scale(-0.5)).add(right.scale(this.attackWidth));
        Vec3 b2 = pos.add(forward.scale(-0.5)).add(right.scale(-this.attackWidth));
        Vec3 b3 = pos.add(forward.scale(this.attackRange)).add(right.scale(-this.attackWidth));
        Vec3 b4 = pos.add(forward.scale(this.attackRange)).add(right.scale(this.attackWidth));

        Vec3 t1 = b1.add(0, this.attackHeight, 0);
        Vec3 t2 = b2.add(0, this.attackHeight, 0);
        Vec3 t3 = b3.add(0, this.attackHeight, 0);
        Vec3 t4 = b4.add(0, this.attackHeight, 0);

        drawLine(level, b1, b2); drawLine(level, b2, b3); drawLine(level, b3, b4); drawLine(level, b4, b1);
        drawLine(level, t1, t2); drawLine(level, t2, t3); drawLine(level, t3, t4); drawLine(level, t4, t1);
        drawLine(level, b1, t1); drawLine(level, b2, t2); drawLine(level, b3, t3); drawLine(level, b4, t4);
    }

    private void drawLine(ServerLevel level, Vec3 p1, Vec3 p2) {
        double distance = p1.distanceTo(p2);
        int points = (int) (distance * 4.0);

        for (int i = 0; i <= points; i++) {
            double lerp = i / (double) points;
            double x = Mth.lerp(lerp, p1.x, p2.x);
            double y = Mth.lerp(lerp, p1.y, p2.y);
            double z = Mth.lerp(lerp, p1.z, p2.z);
            level.sendParticles(ParticleTypes.BUBBLE, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}