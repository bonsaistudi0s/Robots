package dev.xylonity.bonsai.robots.client.entity.renderer;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.client.entity.layer.TankMechRiderLayer;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TankMechRenderer extends AbstractMechRenderer<TankMechEntity> {

    public TankMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, 1.5f);
        addRenderLayer(new TankMechRiderLayer());
    }

    @Override
    public ResourceLocation getTextureLocation(TankMechEntity tankMechEntity) {
        return Robots.of("textures/entity/tank_mech/tank_mech_black.png");
    }

    @Override
    protected KnightLibModelSource defineModel(TankMechEntity tankMechEntity) {
        return KnightLibModelSource.geo(Robots.of("geo/tank_mech.geo.json"));
    }

}