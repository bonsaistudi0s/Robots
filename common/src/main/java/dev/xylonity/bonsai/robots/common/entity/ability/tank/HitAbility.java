package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HitAbility implements MechAbility {

    private final int durationTicks;
    private final int impactTick;
    private final double range;

    public HitAbility(int durationTicks, int impactTick, double range) {
        if (impactTick < 0 || durationTicks < impactTick) {
            throw new IllegalArgumentException("impactTick must be within the ability duration");
        }
        if (!Double.isFinite(range) || range <= 0.0D) {
            throw new IllegalArgumentException("range must be finite and positive");
        }

        this.durationTicks = durationTicks;
        this.impactTick = impactTick;
        this.range = range;
    }

    @Override
    public int cooldownTicks() {
        return RobotsConfig.TANK_MECH_HIT_COOLDOWN_TICKS;
    }

    @Override
    public int durationTicks() {
        return durationTicks;
    }

    @Override
    public void onActivate(AbstractMechEntity mech, Player pilot) {
        ;;
    }

    @Override
    public void onTick(AbstractMechEntity mech, Player pilot, int ticksActive) {
        if (ticksActive != impactTick) {
            return;
        }

        final AttributeInstance attackDamage = mech.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage == null || attackDamage.getValue() <= 0) {
            return;
        }

        final Vec3 forward = Vec3.directionFromRotation(0.0F, mech.getYRot()).normalize();
        final Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);
        final AABB attackBB = mech.getBoundingBox()
                .expandTowards(forward.scale(range))
                .inflate(0.0D, 0.5D, 0.0D);
        final Vec3 origin = mech.getBoundingBox().getCenter();

        for (final LivingEntity target : mech.level().getEntitiesOfClass(LivingEntity.class, attackBB,
                target ->
                        target != mech && target != pilot && target.isAlive() &&
                        target.isAttackable() && !target.isSpectator() &&
                        target.getRootVehicle() != mech
                )
        ) {
            final Vec3 offset = target.getBoundingBox().getCenter().subtract(origin);
            final double forwardDistance = offset.dot(forward);
            final double maximumForwardDistance = mech.getBbWidth() * 0.5D + range + target.getBbWidth() * 0.5D;
            final double maximumLateralDistance = (mech.getBbWidth() + target.getBbWidth()) * 0.5D;
            if (forwardDistance <= 0.0D || forwardDistance > maximumForwardDistance || Math.abs(offset.dot(right)) > maximumLateralDistance) {
                continue;
            }

            final Vec3 movementBeforeHit = target.getDeltaMovement();
            if (target.hurt(mech.damageSources().mobAttack(mech), (float) attackDamage.getValue())) {
                final Vec3 knockback = target.getDeltaMovement().subtract(movementBeforeHit);
                target.setDeltaMovement(movementBeforeHit.add(knockback.scale(2.0D)));
            }

        }

    }

}