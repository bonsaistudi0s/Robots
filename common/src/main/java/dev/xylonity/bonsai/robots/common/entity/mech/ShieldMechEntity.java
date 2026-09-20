package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.knightlib.api.animation.KnightLibAnim;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationController;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationControllerRegistrar;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class ShieldMechEntity extends AbstractMechEntity {

    private static final KnightLibAnim IDLE = KnightLibAnim.begin().thenLoop("animation.shield_mech.idle");
    private static final KnightLibAnim WALK = KnightLibAnim.begin().thenLoop("animation.shield_mech.walk");
    private static final KnightLibAnim SLEEPING = KnightLibAnim.begin().thenLoop("animation.shield_mech.sleeping");
    private static final KnightLibAnim SLEEP_TRANSITION = KnightLibAnim.begin().thenLoop("animation.shield_mech.sleep_transition");
    private static final KnightLibAnim WAKE_UP_TRANSITION = KnightLibAnim.begin().thenLoop("animation.shield_mech.wake_up_transition");
    private static final KnightLibAnim GROUND_HIT_ABILITY = KnightLibAnim.begin().thenLoop("animation.shield_mech.ground_hit_ability");
    private static final KnightLibAnim ATTACK = KnightLibAnim.begin().thenLoop("animation.shield_mech.attack");
    private static final KnightLibAnim SHIELD_ABILITY_IDLE = KnightLibAnim.begin().thenLoop("animation.shield_mech.shield_ability_idle");
    private static final KnightLibAnim SHIELD_ABILITY_ACTIVATE = KnightLibAnim.begin().thenLoop("animation.shield_mech.shield_ability_activate");
    private static final KnightLibAnim FIRE_UPGRADE_OFF = KnightLibAnim.begin().thenLoop("animation.shield_mech.fire_upgrade_off");
    private static final KnightLibAnim SHIELD_ABILITY_OFF = KnightLibAnim.begin().thenLoop("animation.shield_mech.shield_ability_off");
    private static final KnightLibAnim DEATH = KnightLibAnim.begin().thenLoop("animation.shield_mech.death");
    private static final KnightLibAnim SHIELD = KnightLibAnim.begin().thenLoop("animation.shield_mech.shield");
    private static final KnightLibAnim DEACTIVATED = KnightLibAnim.begin().thenLoop("animation.shield_mech.deactivated");
    private static final KnightLibAnim ACTIVATE = KnightLibAnim.begin().thenPlayAndHold("animation.shield_mech.activate");

    public ShieldMechEntity(EntityType<? extends AbstractMechEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMechAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.16225D)
                .add(Attributes.ATTACK_DAMAGE, RobotsConfig.TANK_MECH_MELEE_DAMAGE);
    }

    @Override
    protected KnightLibAnim getIdleAnimation() {
        return IDLE;
    }

    @Override
    protected KnightLibAnim getMovementAnimation() {
        return WALK;
    }

    @Override
    protected int getActivationDurationTicks() {
        return 51;
    }

    @Override
    public void registerAnimationControllers(KnightLibAnimationControllerRegistrar controllers) {
        controllers.add(KnightLibAnimationController.of("movementController")
                .selects(this::movementPredicate)
                .speed(this::movementAnimationSpeed)
                .transition(5)
        );

        controllers.add(KnightLibAnimationController.of("miscController").selects(() -> SHIELD_ABILITY_OFF));
    }

    private double movementAnimationSpeed(KnightLibAnimationState state) {
        if (!state.isMoving()) {
            return 1.0D;
        }
        if (!this.isSprinting()) {
            final double walkSpeed = state.blocksPerSecond() * getWalkCycleSeconds() / getWalkBlocksPerCycle();
            return Math.min(3.0D, Math.max(0.25D, walkSpeed));
        }

        final float blockFriction = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction();
        final double movementSpeed = this.getAttributeValue(Attributes.MOVEMENT_SPEED) * Math.max(1.0D, this.getSprintSpeedMultiplier());
        final double acceleration;
        final double drag;
        if (this.onGround()) {
            acceleration = movementSpeed * (0.21600002D / (blockFriction * blockFriction * blockFriction));
            drag = blockFriction * 0.91D;
        }
        else {
            acceleration = movementSpeed * 0.1D;
            drag = 0.91D;
        }

        final double sprintSpeed = acceleration / Math.max(1.0E-6D, 1.0D - drag);
        return Math.min(1.0D, state.blocksPerTick() / sprintSpeed);
    }

    private KnightLibAnim movementPredicate(KnightLibAnimationState state) {
        if (this.isDeadOrDying()) {
            return DEATH;
        }
        if (this.isDeactivated()) {
            return DEACTIVATED;
        }
        if (this.isActivating()) {
            return ACTIVATE;
        }
        if (state.isMoving()) {
            return WALK;
        }
        else {
            return IDLE;
        }

    }

}
