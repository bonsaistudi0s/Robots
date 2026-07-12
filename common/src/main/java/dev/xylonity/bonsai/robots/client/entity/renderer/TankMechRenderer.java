package dev.xylonity.bonsai.robots.client.entity.renderer;

import dev.xylonity.bonsai.robots.client.entity.model.TankMechModel;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TankMechRenderer extends GeoEntityRenderer<TankMechEntity> {

    public TankMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TankMechModel());
        this.shadowRadius = 2f;
    }

}
