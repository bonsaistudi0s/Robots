package dev.xylonity.bonsai.robots.common.entity.ability.tank;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.MechAbility;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.bonsai.robots.common.entity.projectile.BigLaserProjectileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LazerBigAbility implements MechAbility {

    @Override
    public int cooldownTicks() {
        return 200;
    }

    @Override
    public int aimTicks() {
        return 9;
    }

    @Override
    public int aimReleaseDelayTicks() {
        return 5;
    }

    @Override
    public void onStartAiming(AbstractMechEntity mech, Player pilot) {
        if (mech instanceof TankMechEntity tankMech) {
            tankMech.playAimAnimation();
        }

    }

    @Override
    public void onStopAiming(AbstractMechEntity mech, @Nullable Player pilot) {
        if (mech instanceof TankMechEntity tankMech) {
            tankMech.playAimOffAnimation();
        }

    }

    @Override
    public void onActivate(AbstractMechEntity mech, Player pilot) {
        if (!(mech instanceof TankMechEntity tankMech)) {
            return;
        }

        final Vec3 look = pilot.getLookAngle().normalize();
        final Vec3 right = new Vec3(-look.z, 0.0D, look.x).normalize();
        final Vec3 position = mech.position()
                .add(0.0D, 3.0D, 0.0D) // Upwards
                .add(right.scale(1.0D)) // To the right
                .add(look.scale(2.0D)); // To the front

        final Vec3 eyePosition = pilot.getEyePosition();
        Vec3 target = mech.level().clip(new ClipContext(eyePosition, eyePosition.add(look.scale(64)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mech)).getLocation();
        final EntityHitResult entityAim = ProjectileUtil.getEntityHitResult(mech.level(), mech, eyePosition, target, new AABB(eyePosition, target).inflate(1.0D), e -> !e.isSpectator() && e.isPickable() && e != pilot);
        if (entityAim != null) {
            target = entityAim.getLocation();
        }

        final double along = Math.max(target.subtract(eyePosition).dot(look), 6);
        final Vec3 direction = eyePosition.add(look.scale(along)).subtract(position).normalize();

        final BigLaserProjectileEntity laser = new BigLaserProjectileEntity(mech.level(), mech);

        laser.setPos(position);
        laser.shoot(direction.x, direction.y, direction.z, 3.0F, 0.0F);

        mech.level().addFreshEntity(laser);

        tankMech.playShootHandAnimation();
    }

}
