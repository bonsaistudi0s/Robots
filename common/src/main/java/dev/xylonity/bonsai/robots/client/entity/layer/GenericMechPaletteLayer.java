package dev.xylonity.bonsai.robots.client.entity.layer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.api.util.ResourceLocations;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayer;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class GenericMechPaletteLayer<T extends AbstractMechEntity> extends KnightLibRenderLayer<T> {

    private static final Map<ResourceLocation, Map<DyeColor, Material>> MATERIALS = new ConcurrentHashMap<>();

    private final Function<T, ResourceLocation> spriteSelector;

    public GenericMechPaletteLayer(Function<T, ResourceLocation> spriteSelector) {
        this.spriteSelector = spriteSelector;
    }

    private static Map<DyeColor, Material> createMaterials(ResourceLocation sprite) {
        final Map<DyeColor, Material> materials = new EnumMap<>(DyeColor.class);
        for (final DyeColor color : DyeColor.values()) {
            materials.put(color, new Material(
                    Sheets.ARMOR_TRIMS_SHEET,
                    ResourceLocations.of(sprite.getNamespace(), sprite.getPath() + "_" + color.getName())
            ));

        }

        return materials;
    }

    @Override
    public boolean shouldRender(KnightLibRenderLayerContext<T> context) {
        if (spriteSelector.apply(context.target()) == null) {
            return false;
        }

        final T mech = context.target();
        final Minecraft minecraft = Minecraft.getInstance();
        return !mech.isInvisible() || minecraft.player != null && !mech.isInvisibleTo(minecraft.player) || minecraft.shouldEntityAppearGlowing(mech);
    }

    @Override
    public void render(KnightLibRenderLayerContext<T> context) {
        final ResourceLocation sprite = spriteSelector.apply(context.target());
        if (sprite == null) {
            return;
        }

        final Material material = MATERIALS.computeIfAbsent(sprite, GenericMechPaletteLayer::createMaterials).get(context.target().getDyeColor());
        final VertexConsumer consumer = material.buffer(context.buffers(), RenderType::entityCutoutNoCull);
        context.renderModel(consumer, context.packedLight(), context.packedOverlay(), context.renderColor());
    }

}
