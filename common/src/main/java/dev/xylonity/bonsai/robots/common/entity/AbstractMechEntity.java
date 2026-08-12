package dev.xylonity.bonsai.robots.common.entity;

import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityManager;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityAnimationPhase;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.bonsai.robots.registry.RobotsBlocks;
import dev.xylonity.knightlib.api.animation.*;
import dev.xylonity.knightlib.api.util.KnightLibMath;
import dev.xylonity.knightlib.api.util.ResourceLocations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public abstract class AbstractMechEntity extends PathfinderMob implements KnightLibAnimatable {

    private final KnightLibAnimationHandler animations = KnightLibAnimationHandler.of(this);

    // Identifier for the temp mod that's applied later on to the attack damage attribute (as the electric field computes a damage multiplier)
    private static final UUID ELECTRIC_DAMAGE_MODIFIER_ID = UUID.fromString("5750f66f-c5e2-4dc1-b4fb-780237ab766f");

    // nbt tags
    private static final String TAG_TOGGLED_ABILITY_SLOTS = "ToggledAbilitySlots";
    private static final String TAG_ACTIVATION_STATE = "ActivationState";
    private static final String TAG_ACTIVATION_TICKS = "ActivationTicks";
    private static final String TAG_DYE_COLOR = "DyeColor";

    // Slot for the current ability in use
    private static final EntityDataAccessor<Integer> DATA_ACTIVE_SLOT = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SELECTED_SLOT = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.INT);

    // Bitmask of the special slots whose toggle ability is currently switched on (as the electric field ability is always toggled when switched on)
    private static final EntityDataAccessor<Integer> DATA_TOGGLED_SLOTS = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> DATA_AIMING = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.BOOLEAN);

    // Generic targeting state used by abilities that target entities while aiming (in order to save them over time, for example while targeted but not looking at them)
    private static final EntityDataAccessor<CompoundTag> DATA_ABILITY_TARGETS = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.COMPOUND_TAG);

    // Current energy of the mech
    private static final EntityDataAccessor<Float> DATA_ENERGY = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.FLOAT);

    // All mechs start deactivated and must finish their own activation sequence before they can be used
    private static final EntityDataAccessor<Byte> DATA_ACTIVATION_STATE = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.BYTE);

    // What reskin color is applied
    private static final EntityDataAccessor<Integer> DATA_DYE_COLOR = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.INT);

    private static final byte ACTIVATION_STATE_DEACTIVATED = 0;
    private static final byte ACTIVATION_STATE_ACTIVATING = 1;
    private static final byte ACTIVATION_STATE_ACTIVATED = 2;

    private final AbilityManager abilityManager = new AbilityManager(this);

    private int activationTicksRemaining;

    // Client-only helpers to handle the torso rotation
    public float clientLegsYaw;
    public float clientTorsoYaw;
    public boolean clientYawInitialized;

    // Client-only helpers to handle whether the legs should face the movement direction
    public boolean clientLegsReversed;
    public float clientAimCameraProgress;

    // Client-only helpers to handle leg IKs
    public float clientLeftLegGroundDelta;
    public float clientRightLegGroundDelta;
    public boolean clientLegIkInitialized;

    protected AbstractMechEntity(EntityType<? extends AbstractMechEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createMechAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    @Nullable
    public Player getPilot() {
        return this.getControllingPassenger() instanceof Player player ? player : null;
    }

    public int getActiveSlot() {
        return this.entityData.get(DATA_ACTIVE_SLOT);
    }

    public void setActiveSlot(int slot) {
        this.entityData.set(DATA_ACTIVE_SLOT, slot);
    }

    public int getSelectedSlot() {
        return this.entityData.get(DATA_SELECTED_SLOT);
    }

    public void setSelectedSlot(int slot) {
        this.entityData.set(DATA_SELECTED_SLOT, slot);
    }

    public boolean isToggleActive(int slot) {
        return slot >= 0 && slot < AbilityManager.MAX_SPECIAL_SLOTS && (this.entityData.get(DATA_TOGGLED_SLOTS) & 1 << slot) != 0;
    }

    public void setToggleActive(int slot, boolean active) {
        if (slot < 0 || slot >= AbilityManager.MAX_SPECIAL_SLOTS) {
            return;
        }

        final int slots = this.entityData.get(DATA_TOGGLED_SLOTS);
        this.entityData.set(DATA_TOGGLED_SLOTS, active ? slots | 1 << slot : slots & ~(1 << slot));

        updateElectricAttackDamageModifier();
    }

    /**
     * Whether the given ability is switched on, so the client can query it without knowing its slot
     */
    public boolean isAbilityToggled(ResourceLocation abilityId) {
        for (int slot = 0; slot < AbilityManager.MAX_SPECIAL_SLOTS; slot++) {
            if (isToggleActive(slot) && abilityId.equals(abilityManager.getId(slot))) {
                return true;
            }

        }

        return false;
    }

    public boolean isElectricMode() {
        return this.isActivated() && isAbilityToggled(RobotsAbilities.ELECTRIC_FIELD);
    }

    public boolean isAiming() {
        return this.entityData.get(DATA_AIMING);
    }

    public void setAiming(boolean aiming) {
        this.entityData.set(DATA_AIMING, aiming);
    }

    public int[] getAbilityTargetIds() {
        return this.entityData.get(DATA_ABILITY_TARGETS).getIntArray("Targets");
    }

    public int[] getAbilityAcquiringTargetIds() {
        return this.entityData.get(DATA_ABILITY_TARGETS).getIntArray("AcquiringTargets");
    }

    public boolean areAbilityTargetsLocked() {
        return this.entityData.get(DATA_ABILITY_TARGETS).getBoolean("Locked");
    }

    public void setAbilityTargets(int[] targetIds, boolean locked) {
        setAbilityTargets(locked ? targetIds : new int[0], locked ? new int[0] : targetIds);
    }

    public void setAbilityTargets(int[] lockedTargetIds, int[] acquiringTargetIds) {
        final CompoundTag targets = new CompoundTag();
        targets.putIntArray("Targets", lockedTargetIds);
        targets.putIntArray("AcquiringTargets", acquiringTargetIds);
        targets.putBoolean("Locked", lockedTargetIds.length > 0 && acquiringTargetIds.length == 0);
        if (!targets.equals(this.entityData.get(DATA_ABILITY_TARGETS))) {
            this.entityData.set(DATA_ABILITY_TARGETS, targets);
        }

    }

    public void clearAbilityTargets() {
        this.entityData.set(DATA_ABILITY_TARGETS, new CompoundTag());
    }

    public float getEnergy() {
        return this.entityData.get(DATA_ENERGY);
    }

    public void setEnergy(float energy) {
        this.entityData.set(DATA_ENERGY, Mth.clamp(energy, 0.0F, getMaxEnergy()));
    }

    public float getMaxEnergy() {
        return 100.0F;
    }

    public DyeColor getDyeColor() {
        return DyeColor.byId(this.entityData.get(DATA_DYE_COLOR));
    }

    public void setDyeColor(DyeColor color) {
        this.entityData.set(DATA_DYE_COLOR, color.getId());
    }

    public boolean isActivated() {
        return this.entityData.get(DATA_ACTIVATION_STATE) == ACTIVATION_STATE_ACTIVATED;
    }

    public boolean isActivating() {
        return this.entityData.get(DATA_ACTIVATION_STATE) == ACTIVATION_STATE_ACTIVATING;
    }

    public boolean isDeactivated() {
        return this.entityData.get(DATA_ACTIVATION_STATE) == ACTIVATION_STATE_DEACTIVATED;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ACTIVE_SLOT, -1);
        this.entityData.define(DATA_SELECTED_SLOT, -1);
        this.entityData.define(DATA_TOGGLED_SLOTS, 0);
        this.entityData.define(DATA_AIMING, false);
        this.entityData.define(DATA_ABILITY_TARGETS, new CompoundTag());
        this.entityData.define(DATA_ENERGY, 0.0F);
        this.entityData.define(DATA_ACTIVATION_STATE, ACTIVATION_STATE_DEACTIVATED);
        this.entityData.define(DATA_DYE_COLOR, DyeColor.GREEN.getId());
    }

    @Override
    public void tick() {
        super.tick();

        abilityManager.tick();

        if (!this.level().isClientSide) {
            tickActivation();
            if ((!this.canSprint() || this.getPilot() == null) && this.isSprinting()) {
                this.setSprinting(false);
            }

            updateElectricAttackDamageModifier();
        }

    }

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec, @NotNull InteractionHand hand) {
        final ItemStack heldItem = player.getItemInHand(hand);
        // Apply reskin
        if (heldItem.getItem() instanceof DyeItem dyeItem) {
            final DyeColor color = dyeItem.getDyeColor();
            if (!this.level().isClientSide && color != this.getDyeColor()) {
                this.setDyeColor(color);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.DYE_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Charge the mech
        if (heldItem.is(RobotsBlocks.BATTERY_CELL_CHARGED.get().asItem())) {
            if (this.getEnergy() < this.getMaxEnergy() && !this.level().isClientSide) {
                this.setEnergy(this.getMaxEnergy());
                if (!player.getAbilities().instabuild) {
                    final ItemStack emptyCell = new ItemStack(RobotsBlocks.BATTERY_CELL.get());
                    if (heldItem.getCount() == 1) {
                        player.setItemInHand(hand, emptyCell);
                    }
                    else {
                        heldItem.shrink(1);
                        if (!player.getInventory().add(emptyCell)) {
                            player.drop(emptyCell, false);
                        }

                    }

                }

                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.NEUTRAL, 1.0F, 1.2F);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Activate the mech
        if (!this.isActivated() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide && this.isDeactivated()) {
                startActivation(player);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Ride the mech
        if (this.isActivated() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.interactAt(player, vec, hand);
    }

    @Override
    protected boolean canAddPassenger(@NotNull Entity passenger) {
        return this.isActivated() && super.canAddPassenger(passenger);
    }

    private void startActivation(Player activator) {
        final int duration = Math.max(0, this.getActivationDurationTicks());
        if (duration == 0) {
            this.entityData.set(DATA_ACTIVATION_STATE, ACTIVATION_STATE_ACTIVATED);
            this.onActivationFinished();
            return;
        }

        this.activationTicksRemaining = duration;
        this.entityData.set(DATA_ACTIVATION_STATE, ACTIVATION_STATE_ACTIVATING);

        this.onActivationStarted(activator);
    }

    private void tickActivation() {
        if (!this.isActivating()) {
            return;
        }

        if (this.activationTicksRemaining > 0) {
            --this.activationTicksRemaining;
        }
        if (this.activationTicksRemaining <= 0) {
            this.entityData.set(DATA_ACTIVATION_STATE, ACTIVATION_STATE_ACTIVATED);
            this.onActivationFinished();
        }

    }

    /**
     * Dismounts the player in the closest position to the mech (generally on the right side), to not move it
     */
    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(@NotNull LivingEntity passenger) {
        final BlockPos origin = BlockPos.containing(this.getX(), this.getBoundingBox().minY, this.getZ());
        final int minimumDistance = Mth.ceil((this.getBbWidth() + passenger.getBbWidth()) * 0.5F);
        final int[][] offsets = DismountHelper.offsetsForDirection(Direction.fromYRot(this.getYRot()));

        for (int distance = minimumDistance; distance <= minimumDistance + 1; distance++) {
            for (int[] offset : offsets) {
                final BlockPos side = origin.offset(offset[0] * distance, 0, offset[1] * distance);
                for (int yOffset = 1; yOffset >= -2; yOffset--) {
                    final Vec3 safePosition = DismountHelper.findSafeDismountLocation(
                            passenger.getType(), this.level(), side.offset(0, yOffset, 0), true
                    );
                    if (safePosition != null) {
                        passenger.resetFallDistance();
                        return safePosition;
                    }

                }

            }


        }

        // This is necessary to not damage the player when dismounting the mech
        passenger.fallDistance = -this.getBbHeight();

        return super.getDismountLocationForPassenger(passenger);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    protected void tickRidden(@NotNull Player player, @NotNull Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    // Caps the speed if the legs aren't looking at the look direction (taking into account the legs might be going backwards)
    @Override
    protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 travelVector) {
        Vec3 input = new Vec3(player.xxa, 0.0D, player.zza);
        if (input.horizontalDistanceSqr() < 1.0E-7D) {
            return input;
        }

        if (this.clientYawInitialized) {
            final Vec3 worldInput = input.yRot((float) Math.toRadians(-player.getYRot()));
            final float movementYaw = KnightLibMath.yawAngleOf(worldInput);
            if (Math.abs(KnightLibMath.angleDelta(this.clientLegsYaw, movementYaw)) > 90.0F) {
                input = input.scale(0.5D);
            }

        }
        else if (player.zza < 0.0F) {
            input = input.scale(0.5D);
        }

        return input;
    }

    @Override
    protected float getRiddenSpeed(@NotNull Player player) {
        final double sprintMultiplier = this.canSprint() && this.isSprinting() ? Math.max(1.0D, this.getSprintSpeedMultiplier()) : 1.0D;
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) * sprintMultiplier);
    }

    /**
     * Per-mech sprint multiplier. Sprint (capable) implementations should override this together with {@link #canSprint()} so each mech can expose its
     * own configured movement speed.
     */
    protected double getSprintSpeedMultiplier() {
        return 1.0D;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        abilityManager.save(tag);
        tag.putFloat("Energy", getEnergy());
        tag.putInt(TAG_TOGGLED_ABILITY_SLOTS, this.entityData.get(DATA_TOGGLED_SLOTS));
        tag.putByte(TAG_ACTIVATION_STATE, this.entityData.get(DATA_ACTIVATION_STATE));
        tag.putString(TAG_DYE_COLOR, this.getDyeColor().getName());
        if (this.isActivating()) {
            tag.putInt(TAG_ACTIVATION_TICKS, this.activationTicksRemaining);
        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        abilityManager.load(tag);
        if (tag.contains("Energy")) {
            setEnergy(tag.getFloat("Energy"));
        }
        if (tag.contains(TAG_TOGGLED_ABILITY_SLOTS)) {
            this.entityData.set(DATA_TOGGLED_SLOTS, tag.getInt(TAG_TOGGLED_ABILITY_SLOTS));
        }
        if (tag.contains(TAG_ACTIVATION_STATE)) {
            final byte state = tag.getByte(TAG_ACTIVATION_STATE);
            if (state == ACTIVATION_STATE_ACTIVATED) {
                this.entityData.set(DATA_ACTIVATION_STATE, ACTIVATION_STATE_ACTIVATED);
            }
            else if (state == ACTIVATION_STATE_ACTIVATING && tag.getInt(TAG_ACTIVATION_TICKS) > 0) {
                this.activationTicksRemaining = tag.getInt(TAG_ACTIVATION_TICKS);
                this.entityData.set(DATA_ACTIVATION_STATE, ACTIVATION_STATE_ACTIVATING);
            }
            else {
                this.entityData.set(DATA_ACTIVATION_STATE, ACTIVATION_STATE_DEACTIVATED);
            }

        }

        this.setDyeColor(DyeColor.byName(tag.getString(TAG_DYE_COLOR), DyeColor.GREEN));

        updateElectricAttackDamageModifier();
    }

    private void updateElectricAttackDamageModifier() {
        if (this.level().isClientSide) {
            return;
        }

        final AttributeInstance attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage == null) {
            return;
        }

        final AttributeModifier current = attackDamage.getModifier(ELECTRIC_DAMAGE_MODIFIER_ID);
        final double amount = Math.max(0.0D, RobotsConfig.ELECTRIC_MECH_DAMAGE_MULTIPLIER) - 1.0D;
        if (!this.isElectricMode() || amount == 0.0D) {
            if (current != null) {
                attackDamage.removeModifier(ELECTRIC_DAMAGE_MODIFIER_ID);
            }

            return;
        }

        if (current == null || Double.compare(current.getAmount(), amount) != 0) {
            if (current != null) {
                attackDamage.removeModifier(ELECTRIC_DAMAGE_MODIFIER_ID);
            }

            attackDamage.addTransientModifier(new AttributeModifier(
                    ELECTRIC_DAMAGE_MODIFIER_ID,
                    "Electric field damage",
                    amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));

        }

    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        final boolean electric = this.isElectricMode();
        final float adjustedAmount = electric ? amount * (1.0F - (float) Mth.clamp(RobotsConfig.ELECTRIC_MECH_DAMAGE_REDUCTION, 0.0D, 1.0D)) : amount;
        final boolean damaged = super.hurt(source, adjustedAmount);

        // Applies additional damage to the input damage (if it's melee damage, thus an actual entity) in case the electric field ability is active
        final Entity attacker = source.getEntity();
        if (damaged && electric && attacker != null && attacker != this && source.getDirectEntity() == attacker && !source.is(DamageTypes.THORNS) && !attacker.isPassengerOfSameVehicle(this)) {
            final float retaliation = (float) Math.max(0.0D, RobotsConfig.ELECTRIC_MELEE_RETALIATION_DAMAGE);
            if (retaliation > 0.0F) {
                attacker.hurt(this.damageSources().thorns(this), retaliation);
            }

        }

        return damaged;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= getDeathDurationTicks() && !this.level().isClientSide && !this.isRemoved()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                spawnDeathPoofParticles(serverLevel);
            }

            this.remove(RemovalReason.KILLED);
        }

    }

    protected int getDeathDurationTicks() {
        return 20;
    }

    private void spawnDeathPoofParticles(ServerLevel level) {
        final int count = Mth.clamp(Mth.ceil(this.getBbWidth() * this.getBbHeight() * 0.75F), 3, 10);
        for (int i = 0; i < count; i++) {
            Vec3 direction;
            do {
                direction = new Vec3(
                        this.random.nextDouble() * 2.0D - 1.0D,
                        this.random.nextDouble() * 2.0D - 1.0D,
                        this.random.nextDouble() * 2.0D - 1.0D
                );

            }
            while (direction.lengthSqr() < 1.0E-6D);

            final Vec3 velocity = direction.normalize().scale(0.08D + this.random.nextDouble() * 0.1D);
            level.sendParticles(ParticleTypes.POOF,
                    this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 0.75D,
                    this.getY() + this.random.nextDouble() * this.getBbHeight(),
                    this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 0.75D,
                    0, velocity.x, velocity.y, velocity.z, 1.0D
            );

        }

    }

    /**
     * Basic ability kept on left click whenever no special ability is selected
     */
    @Nullable
    public ResourceLocation getPrimaryAbility() {
        return null;
    }

    /**
     * Basic ability kept on right click whenever no special ability is selected
     */
    @Nullable
    public ResourceLocation getSecondaryAbility() {
        return null;
    }

    /**
     * Special abilities per slot (index 0 -> key 1)
     */
    public List<ResourceLocation> getSpecialAbilities() {
        return List.of();
    }

    /**
     * Texture for the mech icon (not hardcoded inside the robots abilities so possible external mods can add their own robots and abilities)
     */
    public ResourceLocation getAbilityIcon(ResourceLocation abilityId) {
        return ResourceLocations.of(abilityId.getNamespace(), "textures/gui/ability/" + abilityId.getPath() + ".png");
    }

    /**
     * Starts a mech-specific visual for an otherwise reusable ability.
     */
    public void playAbilityAnimation(ResourceLocation abilityId) {
        playAbilityAnimation(abilityId, AbilityAnimationPhase.ACTIVATE);
    }

    /**
     * Starts an animation for a particular phase of an ability
     */
    public void playAbilityAnimation(ResourceLocation abilityId, AbilityAnimationPhase phase) {
        ;;
    }

    /**
     * Number of projectiles emitted by an ability that spawns multiple projectiles (volley ability)
     */
    public int getAbilityProjectileCount(ResourceLocation abilityId) {
        return 4;
    }

    /**
     * Launch points for projectile volley abilities
     */
    public List<Vec3> getAbilityLaunchPositions(ResourceLocation abilityId) {
        final Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
        final Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);
        final Vec3 center = this.position().add(0.0D, this.getBbHeight() * 0.85D, 0.0D).add(forward.scale(-this.getBbWidth() * 0.35D));
        final double lateral = Math.max(0.3D, this.getBbWidth() * 0.3D);

        return List.of(
                center.add(right.scale(lateral)).add(0.0D, 0.2D, 0.0D),
                center.add(right.scale(lateral)).add(0.0D, -0.2D, 0.0D),
                center.add(right.scale(-lateral)).add(0.0D, 0.2D, 0.0D),
                center.add(right.scale(-lateral)).add(0.0D, -0.2D, 0.0D)
        );

    }

    protected abstract KnightLibAnim getIdleAnimation();
    protected abstract KnightLibAnim getMovementAnimation();

    /**
     * Duration of this mech's authored activation animation
     */
    protected abstract int getActivationDurationTicks();

    /**
     * Called once on the server when a player starts activating this mech
     */
    protected void onActivationStarted(Player activator) {
        ;;
    }

    /**
     * Called once on the server after the activation duration has elapsed
     */
    protected void onActivationFinished() {
        ;;
    }

    // Blocks per walk cycle (used to scale the walking animation)
    protected float getWalkBlocksPerCycle() {
        return 5.0F;
    }

    // Walk cycle duration
    protected float getWalkCycleSeconds() {
        return 1F;
    }

    @Override
    public KnightLibAnimationHandler getAnimationHandler() {
        return this.animations;
    }

}