package net.darkblade.robots.entity.projectile;

import net.darkblade.robots.entity.RobotsEntitys;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

public class BigLaserProjectile extends BaseGeoProjectile {

    public BigLaserProjectile(EntityType<? extends BigLaserProjectile> type, Level level) {
        super(type, level);
        this.damage = 8.0f;
        this.maxLifeTicks = 80; // 4 seconds
    }

    public BigLaserProjectile(LivingEntity owner, Level level) {
        super(RobotsEntitys.BIG_LASER.get(), owner, level);
        this.damage = 8.0f;
        this.maxLifeTicks = 80;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> PlayState.STOP));
    }
}
