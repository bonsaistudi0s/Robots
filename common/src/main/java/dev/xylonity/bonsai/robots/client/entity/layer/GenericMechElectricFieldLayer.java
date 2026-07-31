package dev.xylonity.bonsai.robots.client.entity.layer;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.client.animation.layer.impl.KnightLibOverlayLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class GenericMechElectricFieldLayer<T extends AbstractMechEntity> extends KnightLibOverlayLayer<T> {

    public GenericMechElectricFieldLayer(Function<T, ResourceLocation> texture) {
        super(texture);
    }

}
