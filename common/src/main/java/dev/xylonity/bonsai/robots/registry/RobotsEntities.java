package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class RobotsEntities {

    public static final ResourceRegistry<EntityType<?>> ENTITIES = ResourceDispatcher.create(BuiltInRegistries.ENTITY_TYPE, Robots.MOD_ID);

    public static final ResourceEntry<EntityType<TankMechEntity>> TANK_MECH = ENTITIES.registerEntity("tank_mech", TankMechEntity::new, MobCategory.CREATURE, 2f, 3f);

}