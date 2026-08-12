package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.projectile.LaserProjectileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

final class LaserAbilityHelper {

    public static void fire(AbstractMechEntity mech, Player pilot, LaserProjectileEntity laser, double rightOffset) {
        final Vec3 look = pilot.getLookAngle().normalize();
        final Vec3 right = new Vec3(-look.z, 0.0D, look.x).normalize();
        final Vec3 position = mech.position().add(0.0D, 3.0D, 0.0D).add(right.scale(rightOffset)).add(look.scale(2.0D));

        final Vec3 eyePosition = pilot.getEyePosition();
        Vec3 target = mech.level().clip(new ClipContext(
                eyePosition, eyePosition.add(look.scale(64.0D)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mech
        )).getLocation();
        final EntityHitResult entityAim = ProjectileUtil.getEntityHitResult(
                mech.level(), mech, eyePosition, target, new AABB(eyePosition, target).inflate(1.0D),
                entity -> !entity.isSpectator() && entity.isPickable() && entity != pilot
        );
        if (entityAim != null) {
            target = entityAim.getLocation();
        }

        final double along = Math.max(target.subtract(eyePosition).dot(look), 6.0D);
        final Vec3 direction = eyePosition.add(look.scale(along)).subtract(position).normalize();
        laser.setPos(position);
        laser.shoot(direction.x, direction.y, direction.z, 3.0F, 0.0F);

        mech.level().addFreshEntity(laser);
    }

}