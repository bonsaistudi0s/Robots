package net.darkblade.robots.entity;

import net.darkblade.robots.entity.ai.MechMeleeAttackGoal;
import net.darkblade.robots.entity.controller.MechManualAttackController;
import net.darkblade.robots.entity.controller.MechRangedAttackController;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

public class TankMechEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> MECH_STATE = SynchedEntityData.defineId(TankMechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING = SynchedEntityData.defineId(TankMechEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_RANGED_STATE = SynchedEntityData.defineId(TankMechEntity.class, EntityDataSerializers.INT);

    public static final int STATE_DEACTIVATED = 0;
    public static final int STATE_ACTIVATING = 1;
    public static final int STATE_ACTIVE = 2;

    private int activationTicks = 0;

    private final MechManualAttackController manualAttackController = new MechManualAttackController(this);
    private final MechRangedAttackController rangedAttackController = new MechRangedAttackController(this);

    public TankMechEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation nav = new GroundPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.getNodeEvaluator().setCanPassDoors(true);
        return nav;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MECH_STATE, STATE_DEACTIVATED);
        this.entityData.define(DATA_ATTACKING, false);
        this.entityData.define(DATA_RANGED_STATE, 0);
    }

    public int getMechState() { return this.entityData.get(MECH_STATE); }
    public void setMechState(int state) { this.entityData.set(MECH_STATE, state); }

    public boolean isAttacking() { return this.entityData.get(DATA_ATTACKING); }
    public void setAttacking(boolean attacking) { this.entityData.set(DATA_ATTACKING, attacking); }

    public int getRangedState() { return this.entityData.get(DATA_RANGED_STATE); }
    public void setRangedState(int state) { this.entityData.set(DATA_RANGED_STATE, state); }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MechState", this.getMechState());
        compound.putInt("ActivationTicks", this.activationTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setMechState(compound.getInt("MechState"));
        this.activationTicks = compound.getInt("ActivationTicks");
    }

    @Override
    protected void registerGoals() {
        /*
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MechMeleeAttackGoal<>(
                this, 3.0, 2.0, 3.5, 0.0, 0.0, 2.0, 5.0, 1.2, 18, new int[]{4, 5, 6}, 0, this::setAttacking
        ));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Cow.class, true));
        */
    }

    public void triggerManualAttack() {
        if (!this.level().isClientSide() && this.getMechState() == STATE_ACTIVE) {
            this.manualAttackController.trigger();
        }
    }

    public void triggerManualShoot() {
        if (!this.level().isClientSide() && this.getMechState() == STATE_ACTIVE) {
            this.rangedAttackController.trigger();
        }
    }

    // ─────────── Riding ───────────

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.getMechState() == STATE_ACTIVE) {
            Entity first = this.getFirstPassenger();
            if (first instanceof Player player) return player;
        }
        return null;
    }

    @Override
    public boolean shouldRiderSit() { return true; }

    @Override
    protected Vec3 getRiddenInput(Player rider, Vec3 travelVector) {
        float forward = rider.zza;
        float strafe  = rider.xxa;
        if (forward < 0) forward *= 0.25f;
        return new Vec3(strafe * 0.4f, 0, forward);
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    protected void tickRidden(Player rider, Vec3 travelVector) {
        super.tickRidden(rider, travelVector);
        this.setRot(rider.getYRot(), this.getXRot() * 0.5f);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            int state = this.getMechState();

            if (state == STATE_DEACTIVATED) {
                this.setMechState(STATE_ACTIVATING);
                this.activationTicks = 0;
                return InteractionResult.SUCCESS;
            }
            else if (state == STATE_ACTIVE && !this.hasPassenger(player)) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            int state = this.getMechState();

            if (state == STATE_DEACTIVATED || state == STATE_ACTIVATING) {
                this.setNoAi(true);
            } else {
                this.setNoAi(this.getControllingPassenger() != null);
            }

            if (state == STATE_ACTIVATING) {
                this.activationTicks++;
                if (this.activationTicks >= 58) {
                    this.setMechState(STATE_ACTIVE);
                }
            }

            if (state == STATE_ACTIVE) {
                this.manualAttackController.tick();
                this.rangedAttackController.tick();
            }
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (!this.hasPassenger(passenger)) return;
        float yaw = this.yBodyRot * Mth.DEG_TO_RAD;
        float sin = Mth.sin(yaw);
        float cos = Mth.cos(yaw);
        float offsetX = 0.0f;
        float offsetZ = 0.0f;
        double x = this.getX() + (-sin * offsetZ + cos * offsetX);
        double z = this.getZ() + ( cos * offsetZ + sin * offsetX);
        double y = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
        callback.accept(passenger, x, y, z);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movementController", 5, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "attackController", 2, this::attackPredicate));
        controllers.add(new AnimationController<>(this, "aimController", 3, this::aimPredicate));
        controllers.add(new AnimationController<>(this, "shootOverlay", 0, this::shootOverlayPredicate));
    }

    private PlayState movementPredicate(AnimationState<TankMechEntity> event) {
        int state = this.getMechState();
        if (state == STATE_DEACTIVATED) {
            event.getController().transitionLength(0);
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.tank_mech.activate_idle"));
            return PlayState.CONTINUE;
        }
        event.getController().transitionLength(5);
        if (state == STATE_ACTIVATING) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.tank_mech.activate"));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.tank_mech.walk"));
        } else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.tank_mech.idle"));
        }
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState<TankMechEntity> event) {
        if (this.isAttacking() && this.getMechState() == STATE_ACTIVE) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.tank_mech.attack"));
            return PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private int prevRangedState = 0;

    private PlayState aimPredicate(AnimationState<TankMechEntity> event) {
        int state = this.getRangedState();
        AnimationController<TankMechEntity> ctrl = event.getController();

        if (state >= 1 && state <= 3) {
            ctrl.transitionLength(3);
            ctrl.setAnimation(
                    RawAnimation.begin().thenPlayAndHold("animation.tank_mech.aim")
            );
            prevRangedState = state;
            return PlayState.CONTINUE;
        }
        else if (state == 4) {
            if (prevRangedState != 4) {
                ctrl.transitionLength(0);
                ctrl.setAnimation(
                        RawAnimation.begin().thenPlay("animation.tank_mech.aim_off")
                );
            }
            prevRangedState = 4;
            return PlayState.CONTINUE;
        }

        prevRangedState = 0;
        ctrl.transitionLength(3);
        ctrl.forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState shootOverlayPredicate(AnimationState<TankMechEntity> event) {
        if (this.getRangedState() == 2) {
            event.getController().setAnimation(
                    RawAnimation.begin().thenPlay("animation.tank_mech.shoot_hand")
            );
            return PlayState.CONTINUE;
        }

        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 3.5;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}