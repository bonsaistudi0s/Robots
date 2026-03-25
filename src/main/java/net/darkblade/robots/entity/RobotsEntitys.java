package net.darkblade.robots.entity;

import net.darkblade.robots.Robots;
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

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}