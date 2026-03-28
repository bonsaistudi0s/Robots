package net.darkblade.robots.entity;

import net.darkblade.robots.Robots;
import net.darkblade.robots.entity.projectile.BigLaserProjectile;
import net.darkblade.robots.entity.projectile.LaserProjectile;
import net.darkblade.robots.entity.projectile.RocketProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RobotsEntitys {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Robots.MODID);

    public static final RegistryObject<EntityType<TankMechEntity>> TANK_MECH =
            ENTITIES.register("tank_mech",
                    () -> EntityType.Builder.of(TankMechEntity::new, MobCategory.CREATURE)
                            .sized(2.5f, 4.0f)
                            .build("tank_mech"));

    public static final RegistryObject<EntityType<LaserProjectile>> LASER =
            ENTITIES.register("laser",
                    () -> EntityType.Builder.<LaserProjectile>of(LaserProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("laser"));

    public static final RegistryObject<EntityType<BigLaserProjectile>> BIG_LASER =
            ENTITIES.register("big_laser",
                    () -> EntityType.Builder.<BigLaserProjectile>of(BigLaserProjectile::new, MobCategory.MISC)
                            .sized(0.4f, 0.4f)
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("big_laser"));

    public static final RegistryObject<EntityType<RocketProjectile>> ROCKET =
            ENTITIES.register("rocket",
                    () -> EntityType.Builder.<RocketProjectile>of(RocketProjectile::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("rocket"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}