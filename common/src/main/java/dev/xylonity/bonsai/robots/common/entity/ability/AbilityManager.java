package dev.xylonity.bonsai.robots.common.entity.ability;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.network.packets.AbilityCooldownS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Automaton that handles the ticking and main logic abstractions for each ability
 */
public class AbilityManager {

    public static final int MAX_SPECIAL_SLOTS = 5;

    // This is set to change

    public static final int PRIMARY_SLOT = MAX_SPECIAL_SLOTS;
    public static final int SECONDARY_SLOT = MAX_SPECIAL_SLOTS + 1;

    private static final int TOTAL_SLOTS = MAX_SPECIAL_SLOTS + 2;

    private static final String TAG_COOLDOWNS = "AbilityCooldowns";

    private final int[] cooldowns = new int[TOTAL_SLOTS];

    private List<MechAbility> specials;
    private MechAbility primary;
    private MechAbility secondary;
    private boolean resolved;
    private int activeTicks;
    private int aimingTicks;
    private int aimReleaseTicks;
    private int aimingSlot = -1;

    private final AbstractMechEntity mech;

    public AbilityManager(AbstractMechEntity mech) {
        this.mech = mech;
    }

    private void resolve() {
        if (resolved) {
            return;
        }

        final List<ResourceLocation> loadout = mech.getSpecialAbilities();
        final List<MechAbility> list = new ArrayList<>(loadout.size());
        for (final ResourceLocation id : loadout) {
            list.add(resolveOne(id));
        }

        specials = list;

        primary = mech.getPrimaryAbility() != null ? resolveOne(mech.getPrimaryAbility()) : null;
        secondary = mech.getSecondaryAbility() != null ? resolveOne(mech.getSecondaryAbility()) : null;

        resolved = true;
    }

    private static MechAbility resolveOne(ResourceLocation id) {
        final MechAbility ability = AbilityRegistry.get(id);
        if (ability == null) {
            throw new IllegalStateException("[Robots] Unknown mech ability: " + id);
        }

        return ability;
    }

    public int specialSlotCount() {
        resolve();
        return Math.min(specials.size(), MAX_SPECIAL_SLOTS);
    }

    @Nullable
    public MechAbility get(int slot) {
        resolve();
        if (slot == PRIMARY_SLOT) {
            return primary;
        }
        if (slot == SECONDARY_SLOT) {
            return secondary;
        }

        return slot >= 0 && slot < specialSlotCount() ? specials.get(slot) : null;
    }

    @Nullable
    public ResourceLocation getId(int slot) {
        if (slot == PRIMARY_SLOT) {
            return mech.getPrimaryAbility();
        }
        if (slot == SECONDARY_SLOT) {
            return mech.getSecondaryAbility();
        }

        final List<ResourceLocation> ids = mech.getSpecialAbilities();
        return slot >= 0 && slot < Math.min(ids.size(), MAX_SPECIAL_SLOTS) ? ids.get(slot) : null;
    }

    public void tick() {
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) {
                cooldowns[i]--;
            }

        }

        if (mech.level().isClientSide) {
            return;
        }

        final Player pilot = mech.getPilot();
        if (pilot == null) {
            stopActive(true);
            stopAiming(null);
            mech.setSelectedSlot(-1);
            return;
        }

        if (mech.isAiming()) {
            if (aimReleaseTicks > 0) {
                aimReleaseTicks--;
                if (aimReleaseTicks == 0) {
                    stopAiming(pilot);
                }
            }
            else {
                final MechAbility aimedAbility = get(aimingSlot);
                if (aimedAbility == null || aimedAbility.aimTicks() <= 0 || mech.getSelectedSlot() != aimingSlot) {
                    stopAiming(pilot);
                }
                else {
                    aimingTicks++;
                }

            }

        }

        final int active = mech.getActiveSlot();
        if (active < 0) {
            return;
        }

        final MechAbility ability = get(active);
        if (ability == null) {
            stopActive(true);
            return;
        }

        activeTicks++;

        final float energyPerSecond = ability.energyCostPerSecond();
        if (energyPerSecond > 0.0F && activeTicks % 20 == 0) {
            if (mech.getEnergy() < energyPerSecond) {
                stopActive(false);
                return;
            }

            mech.setEnergy(mech.getEnergy() - energyPerSecond);
            if (mech.getEnergy() <= 0.0F) {
                stopActive(false);
                return;
            }

        }

        ability.onTick(mech, pilot, activeTicks);
        if (activeTicks >= ability.durationTicks()) {
            stopActive(false);
        }

    }

    /**
     * Handles cooldowns and tracks the values automatically
     */
    public boolean tryUse(int slot, Player pilot) {
        if (!isValidSlot(slot)) {
            return false;
        }

        final MechAbility ability = get(slot);
        if (ability == null || cooldowns[slot] > 0 || !hasEnergyFor(ability) || !ability.canUse(mech, pilot)) {
            return false;
        }

        if (isSpecialSlot(slot) && mech.getSelectedSlot() != slot) {
            return false;
        }
        if (ability.aimTicks() > 0 && (!mech.isAiming() || aimingSlot != slot || aimingTicks < ability.aimTicks() || aimReleaseTicks > 0)) {
            return false;
        }

        stopActive(true);

        ability.onActivate(mech, pilot);
        mech.setEnergy(mech.getEnergy() - ability.energyCost());
        cooldowns[slot] = ability.cooldownTicks();

        if (ability.durationTicks() > 0) {
            mech.setActiveSlot(slot);
            activeTicks = 0;
        }

        if (isSpecialSlot(slot)) {
            mech.setSelectedSlot(-1);
        }

        if (ability.aimTicks() > 0) {
            aimReleaseTicks = ability.aimReleaseDelayTicks();
            if (aimReleaseTicks <= 0) stopAiming(pilot);
        }

        if (pilot instanceof ServerPlayer serverPilot) {
            Robots.NETWORK.sendTo(serverPilot, AbilityCooldownS2CPacket.TYPE.base(), new AbilityCooldownS2CPacket(mech.getId(), slot, ability.cooldownTicks()));
        }

        return true;
    }

    // The methods below here are set to change

    public void selectSpecial(int slot, Player pilot) {
        final MechAbility ability = isSpecialSlot(slot) ? get(slot) : null;
        if (ability != null && cooldowns[slot] > 0) {
            return;
        }

        final int selected = ability != null && mech.getSelectedSlot() != slot ? slot : -1;
        if (selected == mech.getSelectedSlot()) {
            return;
        }

        stopAiming(pilot);

        mech.setSelectedSlot(selected);
    }

    // Right click over an special ability (if it requires it)
    public boolean startAiming(int slot, Player pilot) {
        if (!isSpecialSlot(slot) || mech.getSelectedSlot() != slot || mech.isAiming()) {
            return false;
        }

        final MechAbility ability = get(slot);
        if (ability == null || ability.aimTicks() <= 0 || cooldowns[slot] > 0 || !hasEnergyFor(ability) || !ability.canUse(mech, pilot)) {
            return false;
        }

        stopActive(true);

        aimingTicks = 0;
        aimReleaseTicks = 0;
        aimingSlot = slot;

        mech.setAiming(true);

        ability.onStartAiming(mech, pilot);

        return true;
    }

    private void stopAiming(@Nullable Player pilot) {
        if (!mech.isAiming()) {
            return;
        }

        final MechAbility ability = get(aimingSlot);

        mech.setAiming(false);

        aimingTicks = 0;
        aimReleaseTicks = 0;
        aimingSlot = -1;

        if (ability != null) {
            ability.onStopAiming(mech, pilot);
        }

    }

    private boolean hasEnergyFor(MechAbility ability) {
        return mech.getEnergy() >= Math.max(ability.energyCost(), ability.energyCostPerSecond());
    }

    private static boolean isSpecialSlot(int slot) {
        return slot >= 0 && slot < MAX_SPECIAL_SLOTS;
    }

    private static boolean isValidSlot(int slot) {
        return slot >= 0 && slot < TOTAL_SLOTS;
    }

    private void stopActive(boolean interrupted) {
        final int active = mech.getActiveSlot();
        if (active < 0) {
            return;
        }

        final MechAbility ability = get(active);
        if (ability != null) {
            ability.onEnd(mech, mech.getPilot(), interrupted);
        }

        mech.setActiveSlot(-1);

        activeTicks = 0;
    }

    public void setCooldownClient(int slot, int ticks) {
        if (slot >= 0 && slot < cooldowns.length) {
            cooldowns[slot] = ticks;
        }

    }

    public boolean isOnCooldown(int slot) {
        return isValidSlot(slot) && cooldowns[slot] > 0;
    }

    public boolean isEnergyDrainActive() {
        final MechAbility ability = get(mech.getActiveSlot());
        return ability != null && ability.energyCostPerSecond() > 0.0F;
    }

    // 1 just used, 0 available
    public float getCooldownProgress(int slot) {
        final MechAbility ability = get(slot);
        if (ability == null || ability.cooldownTicks() <= 0) {
            return 0.0F;
        }

        return cooldowns[slot] / (float) ability.cooldownTicks();
    }

    public void save(CompoundTag tag) {
        tag.putIntArray(TAG_COOLDOWNS, cooldowns.clone());
    }

    public void load(CompoundTag tag) {
        final int[] saved = tag.getIntArray(TAG_COOLDOWNS);
        System.arraycopy(saved, 0, cooldowns, 0, Math.min(saved.length, cooldowns.length));
    }

}