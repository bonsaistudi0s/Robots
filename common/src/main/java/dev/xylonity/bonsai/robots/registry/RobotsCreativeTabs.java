package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class RobotsCreativeTabs {

    public static final ResourceRegistry<CreativeModeTab> CREATIVE_TABS = ResourceDispatcher.create(BuiltInRegistries.CREATIVE_MODE_TAB, Robots.MOD_ID);

    public static final Supplier<CreativeModeTab> ROBOTS = CREATIVE_TABS.register(
            "robots",
            () -> KnightLib.PLATFORM.creativeTabBuilder()
                    .icon(() -> new ItemStack(RobotsBlocks.BATTERY_CELL.get()))
                    .title(Component.translatable("creativetab.robots.title"))
                    .displayItems((parameters, output) -> {
                        output.accept(RobotsBlocks.BATTERY_CELL.get());
                        output.accept(RobotsBlocks.BATTERY_CELL_CHARGED.get());
                        output.accept(RobotsItems.TANK_MECH_SPAWN_EGG.get());
                        output.accept(RobotsItems.TALL_MECH_SPAWN_EGG.get());
                    })
                    .build()
    );

}