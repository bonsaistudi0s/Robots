package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.block.BatteryCellBlock;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class RobotsBlocks {

    public static final ResourceRegistry<Block> BLOCKS = ResourceDispatcher.create(BuiltInRegistries.BLOCK, Robots.MOD_ID);

    public static final ResourceEntry<BatteryCellBlock> BATTERY_CELL = BLOCKS.registerBlock(
            "battery_cell",
            () -> new BatteryCellBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion(), false),
            RobotsItems.ITEMS,
            block -> new BlockItem(block, new Item.Properties())
    );

    public static final ResourceEntry<BatteryCellBlock> BATTERY_CELL_CHARGED = BLOCKS.registerBlock(
            "battery_cell_charged",
            () -> new BatteryCellBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> 7)
                    .noOcclusion(), true),
            RobotsItems.ITEMS,
            block -> new BlockItem(block, new Item.Properties())
    );

}