package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.mech.ShieldMechEntity;
import dev.xylonity.bonsai.robots.common.entity.mech.TallMechEntity;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.bonsai.robots.common.entity.projectile.BigLaserProjectileEntity;
import dev.xylonity.bonsai.robots.common.entity.projectile.RocketProjectileEntity;
import dev.xylonity.bonsai.robots.common.entity.projectile.SmallLaserProjectileEntity;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.List;

public class RobotsEntities {

    public static final ResourceRegistry<EntityType<?>> ENTITIES = ResourceDispatcher.create(BuiltInRegistries.ENTITY_TYPE, Robots.MOD_ID);

    public static final ResourceEntry<EntityType<TankMechEntity>> TANK_MECH = ENTITIES.registerEntity("tank_mech", TankMechEntity::new, MobCategory.CREATURE, 1.85f, 3.4f);
    public static final ResourceEntry<EntityType<TallMechEntity>> TALL_MECH = ENTITIES.registerEntity("tall_mech", TallMechEntity::new, MobCategory.CREATURE, 1.85f, 4f);
    public static final ResourceEntry<EntityType<ShieldMechEntity>> SHIELD_MECH = ENTITIES.registerEntity("shield_mech", ShieldMechEntity::new, MobCategory.CREATURE, 2.5f, 2.5f);

    public static final ResourceEntry<EntityType<SmallLaserProjectileEntity>> SMALL_LASER_PROJECTILE = ENTITIES.registerEntity("small_laser", SmallLaserProjectileEntity::new, MobCategory.MISC, 0.25F, 0.25F,
            List.of(builder -> builder.noSave().clientTrackingRange(8).updateInterval(1)));
    public static final ResourceEntry<EntityType<BigLaserProjectileEntity>> BIG_LASER_PROJECTILE = ENTITIES.registerEntity("big_laser", BigLaserProjectileEntity::new, MobCategory.MISC, 0.5F, 0.5F,
            List.of(builder -> builder.noSave().clientTrackingRange(8).updateInterval(1)));
    public static final ResourceEntry<EntityType<RocketProjectileEntity>> ROCKET_PROJECTILE = ENTITIES.registerEntity("rocket", RocketProjectileEntity::new, MobCategory.MISC, 0.6F, 0.6F,
            List.of(builder -> builder.noSave().clientTrackingRange(10).updateInterval(1)));

}
