package dev.xylonity.bonsai.robots.common.entity;

import dev.xylonity.bonsai.robots.common.entity.ability.AbilityManager;
import dev.xylonity.knightlib.api.animation.*;
import dev.xylonity.knightlib.api.util.KnightLibMath;
import dev.xylonity.knightlib.api.util.ResourceLocations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractMechEntity extends PathfinderMob implements KnightLibAnimatable {

    private final KnightLibAnimationHandler animations = KnightLibAnimationHandler.of(this);

    // Slot for the current ability in use
    private static final EntityDataAccessor<Integer> DATA_ACTIVE_SLOT = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SELECTED_SLOT = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> DATA_AIMING = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.BOOLEAN);

    // _Current_ energy of the mech
    private static final EntityDataAccessor<Float> DATA_ENERGY = SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.FLOAT);

    private final AbilityManager abilityManager = new AbilityManager(this);

    public float clientLegsYaw;
    public float clientTorsoYaw;
    public boolean clientYawInitialized;

    public boolean clientLegsReversed;
    public float clientAimCameraProgress;

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

    public boolean isAiming() {
        return this.entityData.get(DATA_AIMING);
    }

    public void setAiming(boolean aiming) {
        this.entityData.set(DATA_AIMING, aiming);
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

    public float getEnergyRegenPerTick() {
        return 0.05F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ACTIVE_SLOT, -1);
        this.entityData.define(DATA_SELECTED_SLOT, -1);
        this.entityData.define(DATA_AIMING, false);
        this.entityData.define(DATA_ENERGY, getMaxEnergy());
    }

    @Override
    public void tick() {
        super.tick();

        abilityManager.tick();

        if (!this.level().isClientSide && !abilityManager.isEnergyDrainActive() && this.getEnergy() < this.getMaxEnergy()) {
            this.setEnergy(this.getEnergy() + this.getEnergyRegenPerTick());
        }

    }

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec, @NotNull InteractionHand hand) {
        if (!this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.interactAt(player, vec, hand);
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
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        abilityManager.save(tag);
        tag.putFloat("Energy", getEnergy());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        abilityManager.load(tag);
        if (tag.contains("Energy")) {
            setEnergy(tag.getFloat("Energy"));
        }

    }

    // Logic below here is set to change, as the primary and secondary abilities (yet to be confirmed) are selected by pressing 4 (assuming there are only
    // 3 abilities per mech)

    // Left click
    @Nullable
    public ResourceLocation getPrimaryAbility() {
        return null;
    }

    // Right click
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

    protected abstract KnightLibAnim getIdleAnimation();
    protected abstract KnightLibAnim getMovementAnimation();

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