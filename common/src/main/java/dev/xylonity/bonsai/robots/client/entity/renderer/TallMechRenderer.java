package dev.xylonity.bonsai.robots.client.entity.renderer;

import dev.xylonity.bonsai.robots.client.entity.model.TallMechModel;
import dev.xylonity.bonsai.robots.common.entity.mech.TallMechEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TallMechRenderer extends AbstractMechRenderer<TallMechEntity> {

    public TallMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TallMechModel());
        this.shadowRadius = 1.0f;
    }

}
