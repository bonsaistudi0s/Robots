package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.movement.GroundMechLocomotion;
import dev.xylonity.bonsai.robots.common.entity.movement.MechLocomotion;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityAnimationPhase;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.bonsai.robots.registry.RobotsSounds;
import dev.xylonity.knightlib.api.animation.*;
import dev.xylonity.knightlib.api.entity.hitbox.BoneHitboxRig;
import dev.xylonity.knightlib.api.entity.hitbox.BoneHitboxRigs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TallMechEntity extends AbstractMechEntity {

    private static final KnightLibAnim WALK = KnightLibAnim.begin().thenLoop("animation.tall_mech.walk");
    private static final KnightLibAnim RUN = KnightLibAnim.begin().thenLoop("animation.tall_mech.run");
    private static final KnightLibAnim IDLE = KnightLibAnim.begin().thenLoop("animation.tall_mech.idle");
    private static final KnightLibAnim JUMP = KnightLibAnim.begin().thenPlay("animation.tall_mech.jump");
    private static final KnightLibAnim FALL = KnightLibAnim.begin().thenPlay("animation.tall_mech.fall");
    private static final KnightLibAnim AIM_ON = KnightLibAnim.begin().thenPlayAndHold("animation.tall_mech.aim_on");
    private static final KnightLibAnim AIM_OFF = KnightLibAnim.begin().thenPlay("animation.tall_mech.aim_off");
    private static final KnightLibAnim ROCKET_UPGRADE_OFF = KnightLibAnim.begin().thenPlay("animation.tall_mech.rocket_upgrade_off");
    private static final KnightLibAnim EXTRA_GUNS_UPGRADE_ON = KnightLibAnim.begin().thenPlay("animation.tall_mech.extra_guns_upgrade_on");
    private static final KnightLibAnim EXTRA_GUNS_UPGRADE_OFF = KnightLibAnim.begin().thenPlay("animation.tall_mech.extra_guns_upgrade_off");
    private static final KnightLibAnim DEATH = KnightLibAnim.begin().thenPlay("animation.tall_mech.death");
    private static final KnightLibAnim ACTIVATE = KnightLibAnim.begin().thenPlayAndHold("animation.tall_mech.activate");
    private static final KnightLibAnim DEACTIVATED = KnightLibAnim.begin().thenLoop("animation.tall_mech.deactivated");
    private static final KnightLibAnim SHOOT = KnightLibAnim.begin().thenPlay("animation.tall_mech.shoot");
    private static final KnightLibAnim SLEEPING = KnightLibAnim.begin().thenPlay("animation.tall_mech.sleeping");
    private static final KnightLibAnim WAKE_UP_TRANSITION = KnightLibAnim.begin().thenPlay("animation.tall_mech.wake_up_transition");
    private static final KnightLibAnim SLEEP_TRANSITION = KnightLibAnim.begin().thenPlay("animation.tall_mech.sleep_transition");

    // walk 8.7 blocks per 1s loop and run 17.3 blocks per 0.5s loop
    private static final MechLocomotion LOCOMOTION = new GroundMechLocomotion(8.7D, 1.0D, 17.3D, 0.5D, () -> RobotsConfig.TALL_MECH_SPRINT_SPEED_MULTIPLIER, () -> RobotsConfig.TALL_MECH_BACKWARD_SPEED_MULTIPLIER);

    public TallMechEntity(EntityType<? extends TallMechEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public MechLocomotion getLocomotion() {
        return LOCOMOTION;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMechAttributes()
                .add(Attributes.MAX_HEALTH, RobotsConfig.TALL_MECH_MAX_HEALTH)
                .add(Attributes.KNOCKBACK_RESISTANCE, RobotsConfig.TALL_MECH_KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, 0.22165D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D);
    }

    @Override
    @Nullable
    public ResourceLocation getPrimaryAbility() {
        return null;
    }

    @Override
    @Nullable
    public ResourceLocation getSecondaryAbility() {
        return null;
    }

    @Override
    public List<ResourceLocation> getSpecialAbilities() {
        return List.of();
    }

    @Override
    public ResourceLocation getAbilityIcon(ResourceLocation abilityId) {
        return new ResourceLocation(abilityId.getNamespace(), "textures/entity/tall_mech/icons/" + abilityId.getPath() + "_icon.png");
    }

    @Override
    public void playAbilityAnimation(ResourceLocation abilityId, AbilityAnimationPhase phase) {

    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            final double y = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
            callback.accept(passenger, this.getX(), y + 1, this.getZ());
        }

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
        return 26;
    }

    @Override
    protected void onActivationStarted(Player activator) {
        this.playSound(RobotsSounds.TALL_MECH_ACTIVATE.get());
    }

    @Override
    public void registerAnimationControllers(KnightLibAnimationControllerRegistrar controllers) {
        controllers.add(KnightLibAnimationController.of("movementController")
                .selects(this::movementPredicate)
                .speed(this::movementAnimationSpeed)
                .transition(5)
        );

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
            return this.isSprinting() ? RUN : WALK;
        }
        else {
            return IDLE;
        }

    }

    @Override
    public void onAnimationKeyframe(KnightLibKeyframeEvent event) {
        if (event.type() != KnightLibKeyframeEvent.Type.SOUND) {
            return;
        }

        if ("animation.tall_mech.idle".equals(event.animation())) {
            if ("idle".equals(event.payload())) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), RobotsSounds.TALL_MECH_IDLE.get(), this.getSoundSource(), 1.0F, 1.0F, false);
            }

            return;
        }

        if (!("animation.tall_mech.walk".equals(event.animation()) || "animation.tall_mech.run".equals(event.animation()))) {
            return;
        }

        final SoundEvent sound = switch (event.payload()) {
            case "walk_1" -> RobotsSounds.TALL_MECH_WALK_1.get();
            case "walk_2" -> RobotsSounds.TALL_MECH_WALK_2.get();
            default -> null;
        };
        if (sound != null) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), 1.0F, 1.0F, false);
            Robots.PROXY.onMechFootstep(this);
        }

    }

}
