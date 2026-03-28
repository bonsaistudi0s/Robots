package net.darkblade.robots.entity.projectile;

import net.darkblade.robots.entity.RobotsEntitys;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

public class LaserProjectile extends BaseGeoProjectile {

    public LaserProjectile(EntityType<? extends LaserProjectile> type, Level level) {
        super(type, level);
        this.damage = 3.0f;
        this.maxLifeTicks = 60; // 3 seconds
    }

    public LaserProjectile(LivingEntity owner, Level level) {
        super(RobotsEntitys.LASER.get(), owner, level);
        this.damage = 3.0f;
        this.maxLifeTicks = 60;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> PlayState.STOP));
    }
}
