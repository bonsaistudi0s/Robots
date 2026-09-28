package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.movement.GroundMechLocomotion;
import dev.xylonity.bonsai.robots.common.entity.movement.MechLocomotion;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityAnimationPhase;
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

    private static final KnightLibAnim IDLE = KnightLibAnim.begin().thenLoop("animation.tall_mech.idle");
    private static final KnightLibAnim WALK = KnightLibAnim.begin().thenLoop("animation.tall_mech.walk");
    private static final KnightLibAnim ATTACK = KnightLibAnim.begin().thenPlay("animation.tall_mech.attack");
    private static final KnightLibAnim AIM = KnightLibAnim.begin().thenPlayAndHold("animation.tall_mech.aim").overridePreviousAnimation();
    private static final KnightLibAnim AIM_OFF = KnightLibAnim.begin().thenPlay("animation.tall_mech.aim_off").overridePreviousAnimation().transition(5);
    private static final KnightLibAnim SHOOT_HAND = KnightLibAnim.begin().thenPlay("animation.tall_mech.shoot_hand").additive();
    private static final KnightLibAnim SPRINT = KnightLibAnim.begin().thenPlay("animation.tall_mech.sprint");
    private static final KnightLibAnim DEATH = KnightLibAnim.begin().thenPlay("animation.tall_mech.death");
    private static final KnightLibAnim EXTRA_GUNS_UPGRADE = KnightLibAnim.begin().thenPlay("animation.tall_mech.extra_guns_upgrade");
    private static final KnightLibAnim ROCKET_ABILITY = KnightLibAnim.begin().thenPlay("animation.tall_mech.rocket_ability");
    private static final KnightLibAnim SHOOT = KnightLibAnim.begin().thenPlay("animation.tall_mech.shoot");
    private static final KnightLibAnim DEACTIVATED = KnightLibAnim.begin().thenLoop("animation.tall_mech.deactivated");
    private static final KnightLibAnim ACTIVATE = KnightLibAnim.begin().thenPlayAndHold("animation.tall_mech.activate");

    public TallMechEntity(EntityType<? extends TallMechEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public MechLocomotion getLocomotion() {
        return GroundMechLocomotion.WALKING;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMechAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
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
            return WALK;
        }
        else {
            return IDLE;
        }

    }

    @Override
    public void onAnimationKeyframe(KnightLibKeyframeEvent event) {
        if (event.type() != KnightLibKeyframeEvent.Type.SOUND || !"animation.tall_mech.walk".equals(event.animation())) {
            return;
        }

        final SoundEvent sound = switch (event.payload()) {
            case "walk_1", "walk_3" -> RobotsSounds.GENERIC_LEG_SWING.get();
            case "walk_2", "walk_4" -> RobotsSounds.GENERIC_STEP.get();
            default -> null;
        };
        if (sound != null) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), 1.0F, 1.0F, false);
        }
        if (sound == RobotsSounds.GENERIC_STEP.get()) {
            Robots.PROXY.onMechFootstep(this);
        }

    }

}
