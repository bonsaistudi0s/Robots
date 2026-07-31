package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class RobotsSounds {

    public static final ResourceRegistry<SoundEvent> SOUNDS = ResourceDispatcher.create(BuiltInRegistries.SOUND_EVENT, Robots.MOD_ID);

    public static final ResourceEntry<SoundEvent> AQUATIC_MECH_ACTIVATE = register("aquatic_mech_activate");
    public static final ResourceEntry<SoundEvent> AQUATIC_MECH_DEATH = register("aquatic_mech_death");
    public static final ResourceEntry<SoundEvent> AQUATIC_MECH_IDLE = register("aquatic_mech_idle");
    public static final ResourceEntry<SoundEvent> AQUATIC_MECH_ON_LAND = register("aquatic_mech_on_land");
    public static final ResourceEntry<SoundEvent> AQUATIC_MECH_SWIM = register("aquatic_mech_swim");
    public static final ResourceEntry<SoundEvent> BARREL_BOT_ACTIVATE = register("barrel_bot_activate");
    public static final ResourceEntry<SoundEvent> BARREL_BOT_STEP = register("barrel_bot_step");
    public static final ResourceEntry<SoundEvent> BARREL_BOT_CLAMP = register("barrel_bot_clamp");
    public static final ResourceEntry<SoundEvent> BARREL_BOT_LONG_WOOSH = register("barrel_bot_long_woosh");
    public static final ResourceEntry<SoundEvent> CRAB_MECH_ACTIVATE = register("crab_mech_activate");
    public static final ResourceEntry<SoundEvent> CRAB_MECH_WALK = register("crab_mech_walk");
    public static final ResourceEntry<SoundEvent> FARM_MECH_FARMING = register("farm_mech_farming");
    public static final ResourceEntry<SoundEvent> FARM_MECH_WALK = register("farm_mech_walk");
    public static final ResourceEntry<SoundEvent> FARM_MECH_WATERING = register("farm_mech_watering");
    public static final ResourceEntry<SoundEvent> FLYING_MECH_ACTIVATE = register("flying_mech_activate");
    public static final ResourceEntry<SoundEvent> FLYING_MECH_DEATH = register("flying_mech_death");
    public static final ResourceEntry<SoundEvent> FLYING_MECH_FLY = register("flying_mech_fly");
    public static final ResourceEntry<SoundEvent> FLYING_MECH_FLY_BOOSTED = register("flying_mech_fly_boosted");
    public static final ResourceEntry<SoundEvent> FLYING_MECH_FLY_END = register("flying_mech_fly_end");
    public static final ResourceEntry<SoundEvent> FLYING_MECH_IDLE = register("flying_mech_idle");
    public static final ResourceEntry<SoundEvent> FLYING_MECH_WALK = register("flying_mech_walk");
    public static final ResourceEntry<SoundEvent> BOOSTER_ABILITY = register("booster_ability");
    public static final ResourceEntry<SoundEvent> GROUND_HIT_ABILITY = register("ground_hit_ability");
    public static final ResourceEntry<SoundEvent> ROCKET_ABILITY = register("rocket_ability");
    public static final ResourceEntry<SoundEvent> ROLL_ABILITY = register("roll_ability");
    public static final ResourceEntry<SoundEvent> AIM = register("aim");
    public static final ResourceEntry<SoundEvent> AIM_OFF = register("aim_off");
    public static final ResourceEntry<SoundEvent> GENERIC_DEATH = register("generic_death");
    public static final ResourceEntry<SoundEvent> GENERIC_ACTIVATE = register("generic_activate");
    public static final ResourceEntry<SoundEvent> GENERIC_HIT = register("generic_hit");
    public static final ResourceEntry<SoundEvent> GENERIC_IMPACT = register("generic_impact");
    public static final ResourceEntry<SoundEvent> GENERIC_LEG_SWING = register("generic_leg_swing");
    public static final ResourceEntry<SoundEvent> GENERIC_STEP = register("generic_step");
    public static final ResourceEntry<SoundEvent> GENERIC_IDLE = register("generic_idle");
    public static final ResourceEntry<SoundEvent> LASER = register("laser");
    public static final ResourceEntry<SoundEvent> SLOW_LASER = register("slow_laser");
    public static final ResourceEntry<SoundEvent> SLOW_ZAP = register("slow_zap");
    public static final ResourceEntry<SoundEvent> ZAP = register("zap");
    public static final ResourceEntry<SoundEvent> MINING_MECH_CLAW_OFF = register("mining_mech_claw_off");
    public static final ResourceEntry<SoundEvent> MINING_MECH_CLAW_ON = register("mining_mech_claw_on");
    public static final ResourceEntry<SoundEvent> MINING_MECH_DEATH = register("mining_mech_death");
    public static final ResourceEntry<SoundEvent> MINING_MECH_DRILL = register("mining_mech_drill");
    public static final ResourceEntry<SoundEvent> MINING_MECH_DRILL_OFF = register("mining_mech_drill_off");
    public static final ResourceEntry<SoundEvent> MINING_MECH_DRILL_ON = register("mining_mech_drill_on");
    public static final ResourceEntry<SoundEvent> MINING_MECH_IDLE = register("mining_mech_idle");
    public static final ResourceEntry<SoundEvent> MINING_MECH_WALK = register("mining_mech_walk");
    public static final ResourceEntry<SoundEvent> ROLLING_MECH_ACTIVATE = register("rolling_mech_activate");
    public static final ResourceEntry<SoundEvent> ROLLING_MECH_CLOSE = register("rolling_mech_close");
    public static final ResourceEntry<SoundEvent> ROLLING_MECH_DEATH = register("rolling_mech_death");
    public static final ResourceEntry<SoundEvent> ROLLING_MECH_OPEN = register("rolling_mech_open");
    public static final ResourceEntry<SoundEvent> ROLLING_MECH_ROLL = register("rolling_mech_roll");
    public static final ResourceEntry<SoundEvent> ROLLING_MECH_WALK = register("rolling_mech_walk");
    public static final ResourceEntry<SoundEvent> SHIELD_MECH_WALK = register("shield_mech_walk");
    public static final ResourceEntry<SoundEvent> SUPPORT_BOT_ACTIVATE = register("support_bot_activate");
    public static final ResourceEntry<SoundEvent> SUPPORT_BOT_DEATH = register("support_bot_death");
    public static final ResourceEntry<SoundEvent> SUPPORT_BOT_HEAL = register("support_bot_heal");
    public static final ResourceEntry<SoundEvent> SUPPORT_BOT_IDLE = register("support_bot_idle");
    public static final ResourceEntry<SoundEvent> TALL_MECH_ACTIVATE = register("tall_mech_activate");
    public static final ResourceEntry<SoundEvent> TALL_MECH_DEATH = register("tall_mech_death");
    public static final ResourceEntry<SoundEvent> TALL_MECH_IDLE = register("tall_mech_idle");
    public static final ResourceEntry<SoundEvent> TALL_MECH_JUMP = register("tall_mech_jump");
    public static final ResourceEntry<SoundEvent> TALL_MECH_WALK = register("tall_mech_walk");
    public static final ResourceEntry<SoundEvent> TANK_MECH_ACTIVATE = register("tank_mech_activate");
    public static final ResourceEntry<SoundEvent> TANK_MECH_DEATH = register("tank_mech_death");
    public static final ResourceEntry<SoundEvent> TANK_MECH_IDLE = register("tank_mech_idle");

    private static ResourceEntry<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(Robots.of(name)));
    }

}