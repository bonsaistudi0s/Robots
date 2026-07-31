package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.bonsai.robots.common.entity.projectile.BigLaserProjectileEntity;
import dev.xylonity.bonsai.robots.common.entity.projectile.LaserProjectileEntity;
import dev.xylonity.bonsai.robots.common.entity.projectile.RocketProjectileEntity;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.List;

public class RobotsEntities {

    public static final ResourceRegistry<EntityType<?>> ENTITIES = ResourceDispatcher.create(BuiltInRegistries.ENTITY_TYPE, Robots.MOD_ID);

    public static final ResourceEntry<EntityType<TankMechEntity>> TANK_MECH = ENTITIES.registerEntity("tank_mech", TankMechEntity::new, MobCategory.CREATURE, 1.85f, 3f);

    public static final ResourceEntry<EntityType<LaserProjectileEntity>> LASER_PROJECTILE = ENTITIES.registerEntity("laser", LaserProjectileEntity::new, MobCategory.MISC, 0.25F, 0.25F,
            List.of(builder -> builder.noSave().clientTrackingRange(8).updateInterval(1)));
    public static final ResourceEntry<EntityType<BigLaserProjectileEntity>> BIG_LASER_PROJECTILE = ENTITIES.registerEntity("big_laser", BigLaserProjectileEntity::new, MobCategory.MISC, 0.5F, 0.5F,
            List.of(builder -> builder.noSave().clientTrackingRange(8).updateInterval(1)));
    public static final ResourceEntry<EntityType<RocketProjectileEntity>> ROCKET_PROJECTILE = ENTITIES.registerEntity("rocket", RocketProjectileEntity::new, MobCategory.MISC, 0.6F, 0.6F,
            List.of(builder -> builder.noSave().clientTrackingRange(10).updateInterval(1)));

}
