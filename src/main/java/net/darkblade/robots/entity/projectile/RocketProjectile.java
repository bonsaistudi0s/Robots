package net.darkblade.robots.entity.projectile;

import net.darkblade.robots.entity.RobotsEntitys;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class RocketProjectile extends BaseGeoProjectile {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.rocket.idle");

    public RocketProjectile(EntityType<? extends RocketProjectile> type, Level level) {
        super(type, level);
        this.damage = 12.0f;
        this.maxLifeTicks = 100; // 5 seconds
    }

    public RocketProjectile(LivingEntity owner, Level level) {
        super(RobotsEntitys.ROCKET.get(), owner, level);
        this.damage = 12.0f;
        this.maxLifeTicks = 100;
    }

    @Override
    protected float getGravity() { return 0.01f; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            event.getController().setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        }));
    }
}
