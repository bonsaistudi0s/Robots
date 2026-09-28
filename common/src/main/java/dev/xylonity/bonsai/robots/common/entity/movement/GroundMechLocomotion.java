package dev.xylonity.bonsai.robots.common.entity.movement;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationState;
import dev.xylonity.knightlib.api.util.KnightLibMath;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.DoubleSupplier;

public final class GroundMechLocomotion implements MechLocomotion {

    public static final GroundMechLocomotion WALKING = new GroundMechLocomotion(5, 1, () -> 1);

    private final double blocksPerCycle;
    private final double cycleSeconds;
    private final DoubleSupplier sprintMultiplier;
    private final DoubleSupplier backwardMultiplier;

    public GroundMechLocomotion(double blocksPerCycle, double cycleSeconds, DoubleSupplier sprintMultiplier) {
        this(blocksPerCycle, cycleSeconds, sprintMultiplier, () -> 0.5);
    }

    public GroundMechLocomotion(double blocksPerCycle, double cycleSeconds, DoubleSupplier sprintMultiplier, DoubleSupplier backwardMultiplier) {
        if (blocksPerCycle <= 0 || cycleSeconds <= 0) {
            throw new IllegalArgumentException("[Robots] Movement cycle distance and duration must be positive");
        }

        this.blocksPerCycle = blocksPerCycle;
        this.cycleSeconds = cycleSeconds;
        this.sprintMultiplier = sprintMultiplier;
        this.backwardMultiplier = backwardMultiplier;
    }

    @Override
    public Vec3 riddenInput(AbstractMechEntity mech, Player pilot, Vec3 travelVector) {
        Vec3 input = new Vec3(pilot.xxa, 0.0D, pilot.zza);
        if (input.horizontalDistanceSqr() < 1.0E-7D) {
            return input;
        }

        // Normalized before scaling as otherwise diagonals keep their extra length and pass straight movement
        if (input.lengthSqr() > 1) {
            input = input.normalize();
        }

        // Walking against the legs is slow and legs already turned towards the movement walk at full speed
        if (mech.clientYawInitialized) {
            final Vec3 worldInput = input.yRot((float) Math.toRadians(-pilot.getYRot()));
            final float movementYaw = KnightLibMath.yawAngleOf(worldInput);
            if (Math.abs(KnightLibMath.angleDelta(mech.clientLegsYaw, movementYaw)) > 90.0F) {
                input = input.scale(backwardMultiplier.getAsDouble());
            }

        }
        else if (pilot.zza < 0.0F) {
            input = input.scale(backwardMultiplier.getAsDouble());
        }
        return input;

        // if (pilot.zza < 0) {
        //     input = input.scale(backwardMultiplier.getAsDouble());
        // }
        //
        // return input;

        // if (mech.clientYawInitialized) {
        //     final Vec3 input = input.yRot((float) Math.toRadians(-pilot.getYRot()));
        //     final float movementYaw = KnightLibMath.yawAngleOf(input);
        //     if (Math.abs(KnightLibMath.angleDelta(mech.clientLegsYaw, movementYaw)) > 90) {
        //         input = input.scale(0.5);
        //     }
        //
        // }
        // else if (pilot.zza < 0) {
        //     input = input.scale(0.5);
        // }
        //
        // return input;
    }

    @Override
    public float riddenSpeed(AbstractMechEntity mech, Player pilot) {
        final double multiplier = canSprint(mech) && mech.isSprinting() ? sprintMultiplier.getAsDouble() : 1;
        return (float) (mech.getAttributeValue(Attributes.MOVEMENT_SPEED) * multiplier);
    }

    @Override
    public boolean canSprint(AbstractMechEntity mech) {
        return sprintMultiplier.getAsDouble() > 1;
    }

    @Override
    public boolean usesLegs() {
        return true;
    }

    @Override
    public double animationSpeed(AbstractMechEntity mech, KnightLibAnimationState state) {
        if (!state.isMoving()) {
            return 1;
        }
        if (!canSprint(mech) || !mech.isSprinting()) {
            final double boost = mech.clientYawInitialized && mech.clientLegsReversed ? 1.1 : 1;
            return Math.min(3.0D, Math.max(0.25D, state.blocksPerSecond() * cycleSeconds / blocksPerCycle * boost));
        }

        final float friction = mech.getMovementSurfaceFriction();
        final double speed = mech.getAttributeValue(Attributes.MOVEMENT_SPEED) * sprintMultiplier.getAsDouble();
        final double acceleration = mech.onGround() ? speed * (0.21600002D / (friction * friction * friction)) : speed * 0.1D;
        final double drag = mech.onGround() ? friction * 0.91D : 0.91D;
        final double sprintSpeed = acceleration / Math.max(1.0E-6D, 1.0D - drag);
        return Math.min(1.0D, state.blocksPerTick() / Math.max(1.0E-6D, sprintSpeed));
    }

}