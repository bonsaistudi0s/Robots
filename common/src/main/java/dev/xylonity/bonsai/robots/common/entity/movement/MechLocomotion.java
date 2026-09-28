package dev.xylonity.bonsai.robots.common.entity.movement;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/// Sealed locomotion (movement) policy that doesn't assume whether an entity walks
public interface MechLocomotion {

    MechLocomotion STATIONARY = new MechLocomotion() {
        ;;
    };

    default Vec3 riddenInput(AbstractMechEntity mech, Player pilot, Vec3 travelVector) {
        return Vec3.ZERO;
    }

    default float riddenSpeed(AbstractMechEntity mech, Player pilot) {
        return 0;
    }

    default boolean canSprint(AbstractMechEntity mech) {
        return false;
    }

    /// Whether the mech turns its legs independently of its torso
    default boolean usesLegs() {
        return false;
    }

    default double animationSpeed(AbstractMechEntity mech, KnightLibAnimationState state) {
        return 1;
    }

}
