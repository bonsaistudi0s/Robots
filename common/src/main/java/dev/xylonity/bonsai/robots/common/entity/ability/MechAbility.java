package dev.xylonity.bonsai.robots.common.entity.ability;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Default definition for a mech ability
 */
public interface MechAbility {

    /**
     * Cooldown ticks after activation
     */
    int cooldownTicks();

    /**
     * Energy cost per use
     */
    default float energyCost() {
        return 0.0F;
    }

    /**
     * Energy cost per second (while it's active)
     */
    default float energyCostPerSecond() {
        return 0.0F;
    }

    /**
     * Duration till the ability is activated
     */
    default int durationTicks() {
        return 0;
    }

    /**
     * Special abilities that stay switched on until their key is pressed again (basic abilities are always selected)
     */
    default boolean isToggle() {
        return false;
    }

    /**
     * Preparation time before the ability is able to be used
     */
    default int aimTicks() {
        return 0;
    }

    /**
     * Whether selecting this special ability should immediately enter its aiming state
     */
    default boolean autoAimOnSelect() {
        return false;
    }

    /**
     * Time till the aim functionality stops (while using it)
     */
    default int aimReleaseDelayTicks() {
        return 0;
    }

    default boolean canUse(AbstractMechEntity mech, Player pilot) {
        return true;
    }

    void onActivate(AbstractMechEntity mech, Player pilot);

    default void onStartAiming(AbstractMechEntity mech, Player pilot) {
        ;;
    }

    default void onAimingTick(AbstractMechEntity mech, Player pilot, int aimingTicks) {
        ;;
    }

    default void onStopAiming(AbstractMechEntity mech, @Nullable Player pilot) {
        ;;
    }

    default void onTick(AbstractMechEntity mech, @Nullable Player pilot, int ticksActive) {
        ;;
    }

    default void onEnd(AbstractMechEntity mech, @Nullable Player pilot, boolean interrupted) {
        ;;
    }

}