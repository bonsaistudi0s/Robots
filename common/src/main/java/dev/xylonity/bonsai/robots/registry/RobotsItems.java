package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class RobotsItems {

    public static final ResourceRegistry<Item> ITEMS = ResourceDispatcher.create(BuiltInRegistries.ITEM, Robots.MOD_ID);

    public static final ResourceEntry<Item> TANK_MECH_SPAWN_EGG = ITEMS.registerSpawnEgg("tank_mech_spawn_egg", RobotsEntities.TANK_MECH, 0x292D2E, 0x77A63D, new Item.Properties());
    public static final ResourceEntry<Item> TALL_MECH_SPAWN_EGG = ITEMS.registerSpawnEgg("tall_mech_spawn_egg", RobotsEntities.TALL_MECH, 0x53633D, 0x272D2E, new Item.Properties());

}