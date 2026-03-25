package net.darkblade.robots.item;

import net.darkblade.robots.Robots;
import net.darkblade.robots.entity.RobotsEntitys;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RobotsItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Robots.MODID);

    public static final RegistryObject<Item> TANK_MECH_SPAWN_EGG = ITEMS.register("tank_mech_spawn_egg",
            () -> new ForgeSpawnEggItem(RobotsEntitys.TANK_MECH, 0x4B5320, 0x1E1E1E, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}