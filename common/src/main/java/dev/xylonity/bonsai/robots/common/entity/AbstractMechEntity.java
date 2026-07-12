package dev.xylonity.bonsai.robots.common.entity;

import dev.xylonity.bonsai.robots.common.entity.ability.AbilityManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public abstract class AbstractMechEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Slot for the current ability in use
    private static final EntityDataAccessor<Integer> DATA_ACTIVE_SLOT =
            SynchedEntityData.defineId(AbstractMechEntity.class, EntityDataSerializers.INT);

    private final AbilityManager abilityManager = new AbilityManager(this);

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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ACTIVE_SLOT, -1);
    }

    @Override
    public void tick() {
        super.tick();
        abilityManager.tick();
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
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

    //@Override
    //protected void tickRidden(Player player, Vec3 travelVector) {
    //    super.tickRidden(player, travelVector);
    //    this.setRot(player.getYRot(), player.getXRot() * 0.5F);
    //    this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    //}

    //@Override
    //protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
    //    return new Vec3(player.xxa * 0.5F, 0.0D, player.zza >= 0.0F ? player.zza : player.zza * 0.25F);
    //}

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        abilityManager.save(tag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        abilityManager.load(tag);
    }

    /**
     * Abilities of this mech per slot (index 0 -> key 1)
     */
    public abstract List<ResourceLocation> getAbilities();

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        ;;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}