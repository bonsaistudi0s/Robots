package dev.xylonity.bonsai.robots.client.entity.renderer;

import dev.xylonity.bonsai.robots.client.entity.model.TankMechModel;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TankMechRenderer extends AbstractMechRenderer<TankMechEntity> {

    public TankMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TankMechModel());
        this.shadowRadius = 1.5f;
    }

}