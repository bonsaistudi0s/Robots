package dev.xylonity.bonsai.robots.registry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class RobotsRenderTypes {

    private static final Function<ResourceLocation, RenderType> ENTITY_EMISSIVE_DEPTH = Util.memoize(texture -> {
        final RenderType emissive = RenderType.entityTranslucentEmissive(texture, false);
        return new RenderType(
                "robots_entity_emissive_depth",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                () -> {
                    emissive.setupRenderState();
                    RenderSystem.depthMask(true);
                },
                emissive::clearRenderState
        ) {
            ;;
        };
        
    });

    public static RenderType entityEmissiveDepth(ResourceLocation texture) {
        return ENTITY_EMISSIVE_DEPTH.apply(texture);
    }

}