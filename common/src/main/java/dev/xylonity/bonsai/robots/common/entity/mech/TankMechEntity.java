package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityAnimationPhase;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.bonsai.robots.registry.RobotsSounds;
import dev.xylonity.knightlib.api.animation.KnightLibAnim;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationController;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationControllerRegistrar;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationState;
import dev.xylonity.knightlib.api.animation.KnightLibKeyframeEvent;
import dev.xylonity.knightlib.api.client.animation.KnightLibAnimation;
import dev.xylonity.knightlib.api.entity.hitbox.BoneHitboxRig;
import dev.xylonity.knightlib.api.entity.hitbox.BoneHitboxRigs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class TankMechEntity extends AbstractMechEntity {

    private static final KnightLibAnim IDLE = KnightLibAnim.begin().thenLoop("animation.tank_mech.idle");
    private static final KnightLibAnim WALK = KnightLibAnim.begin().thenLoop("animation.tank_mech.walk");
    private static final KnightLibAnim ATTACK = KnightLibAnim.begin().thenPlay("animation.tank_mech.attack");
    private static final KnightLibAnim AIM = KnightLibAnim.begin().thenPlayAndHold("animation.tank_mech.aim").overridePreviousAnimation();
    private static final KnightLibAnim AIM_OFF = KnightLibAnim.begin().thenPlay("animation.tank_mech.aim_off").overridePreviousAnimation().transition(5);
    private static final KnightLibAnim SHOOT_HAND = KnightLibAnim.begin().thenPlay("animation.tank_mech.shoot_hand").additive();
    private static final KnightLibAnim SPRINT = KnightLibAnim.begin().thenPlay("animation.tank_mech.sprint");
    private static final KnightLibAnim DEATH = KnightLibAnim.begin().thenPlay("animation.tank_mech.death");
    private static final KnightLibAnim EXTRA_GUNS_UPGRADE = KnightLibAnim.begin().thenPlay("animation.tank_mech.extra_guns_upgrade");
    private static final KnightLibAnim ROCKET_ABILITY = KnightLibAnim.begin().thenPlay("animation.tank_mech.rocket_ability");
    private static final KnightLibAnim SHOOT = KnightLibAnim.begin().thenPlay("animation.tank_mech.shoot");
    private static final KnightLibAnim ACTIVATE = KnightLibAnim.begin().thenPlay("animation.tank_mech.activate");

    private static final ResourceLocation MODEL = Robots.of("geo/tank_mech.geo.json");
    private static final ResourceLocation ANIMATIONS = Robots.of("animations/tank_mech.animation.json");
    private static final List<String> ROCKET_ANCHORS = List.of(
            "rocket_spawn_1", "rocket_spawn_2", "rocket_spawn_3", "rocket_spawn_4"
    );

    private final BoneHitboxRig anchorRig = BoneHitboxRigs.geo(MODEL, ANIMATIONS);

    public TankMechEntity(EntityType<? extends TankMechEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMechAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.16225D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D);
    }

    @Override
    @Nullable
    public ResourceLocation getPrimaryAbility() {
        return RobotsAbilities.HIT;
    }

    @Override
    @Nullable
    public ResourceLocation getSecondaryAbility() {
        return RobotsAbilities.LAZER_SMALL;
    }

    @Override
    public List<ResourceLocation> getSpecialAbilities() {
        return List.of(RobotsAbilities.LAZER_BIG, RobotsAbilities.ROCKET, RobotsAbilities.ELECTRIC_FIELD);
    }

    @Override
    public ResourceLocation getAbilityIcon(ResourceLocation abilityId) {
        return new ResourceLocation(abilityId.getNamespace(), "textures/entity/tank_mech/icons/" + abilityId.getPath() + "_icon.png");
    }

    @Override
    public void playAbilityAnimation(ResourceLocation abilityId, AbilityAnimationPhase phase) {
        if (RobotsAbilities.ROCKET.equals(abilityId) && phase == AbilityAnimationPhase.ACTIVATE) {
            this.playAnimation(ROCKET_ABILITY.controller("abilityController").duration(40));
            this.playSound(RobotsSounds.ROCKET_ABILITY.get());
        }
        else if (RobotsAbilities.HIT.equals(abilityId) && phase == AbilityAnimationPhase.ACTIVATE) {
            this.playAnimation(ATTACK.controller("abilityController").duration(40));
            this.playSound(RobotsSounds.GENERIC_HIT.get());
        }
        else if (RobotsAbilities.LAZER_SMALL.equals(abilityId) && phase == AbilityAnimationPhase.ACTIVATE) {
            this.playAnimation(SHOOT.controller("abilityController").duration(40));
            this.playSound(RobotsSounds.LASER.get());
        }
        else if (RobotsAbilities.LAZER_BIG.equals(abilityId)) {
            switch (phase) {
                case AIM -> {
                    this.playAnimation(AIM.controller("abilityController"));
                    this.playSound(RobotsSounds.AIM.get());
                }
                case ACTIVATE -> {
                    this.playAnimation(SHOOT_HAND.controller("abilityActionController").duration(4));
                    this.playSound(RobotsSounds.SLOW_LASER.get());
                }
                case AIM_OFF -> {
                    this.playAnimation(AIM_OFF.controller("abilityController").duration(9));
                    this.playSound(RobotsSounds.AIM_OFF.get());
                }

            }

        }

    }

    @Override
    public List<Vec3> getAbilityLaunchPositions(ResourceLocation abilityId) {
        if (!RobotsAbilities.ROCKET.equals(abilityId)) {
            return super.getAbilityLaunchPositions(abilityId);
        }

        final Map<String, Vec3> resolved = new LinkedHashMap<>();
        this.anchorRig.updatePose(this, new LinkedHashSet<>(ROCKET_ANCHORS), 1.0F,
                (name, position, rotation, scaleX, scaleY, scaleZ) -> resolved.put(name, position));

        final List<Vec3> anchors = new ArrayList<>(ROCKET_ANCHORS.size());
        for (final String name : ROCKET_ANCHORS) {
            final Vec3 position = resolved.get(name);
            if (position != null) {
                anchors.add(position);
            }

        }

        return anchors.size() == ROCKET_ANCHORS.size() ? anchors : super.getAbilityLaunchPositions(abilityId);
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
    public void registerAnimationControllers(KnightLibAnimationControllerRegistrar controllers) {
        controllers.add(KnightLibAnimationController.of("movementController")
                .selects(this::movementPredicate)
                .movementSpeed(getWalkBlocksPerCycle(), getWalkCycleSeconds())
                .transition(5)
        );

    }

    private KnightLibAnim movementPredicate(KnightLibAnimationState state) {
        if (state.isMoving()) {
            return WALK;
        }
        else {
            return IDLE;
        }

    }

    @Override
    public void onAnimationKeyframe(KnightLibKeyframeEvent event) {
        if (event.type() != KnightLibKeyframeEvent.Type.SOUND || !"animation.tank_mech.walk".equals(event.animation())) {
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

    }

}
