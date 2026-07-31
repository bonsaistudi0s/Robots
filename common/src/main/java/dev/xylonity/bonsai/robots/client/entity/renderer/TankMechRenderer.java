package dev.xylonity.bonsai.robots.client.entity.renderer;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.client.entity.layer.GenericMechElectricFieldLayer;
import dev.xylonity.bonsai.robots.client.entity.layer.TankMechRiderLayer;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TankMechRenderer extends AbstractMechRenderer<TankMechEntity> {

    private static final ResourceLocation ELECTRIC_FIELD = Robots.of("textures/entity/tank_mech/tank_mech_glow_electric.png");

    public TankMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, 1.5f);
        this.legIkEnabled = true;
        addRenderLayer(new TankMechRiderLayer());
        addRenderLayer(new GenericMechElectricFieldLayer<>(mech -> mech.isAbilityToggled(RobotsAbilities.ELECTRIC_FIELD) ? ELECTRIC_FIELD : null));
    }

    @Override
    public ResourceLocation getTextureLocation(TankMechEntity tankMechEntity) {
        return Robots.of("textures/entity/tank_mech/tank_mech_black.png");
    }

    @Override
    protected KnightLibAnimationSource defineAnimations(TankMechEntity entity) {
        return KnightLibAnimationSource.geo(Robots.of("animations/tank_mech.animation.json"));
    }

    @Override
    protected KnightLibModelSource defineModel(TankMechEntity tankMechEntity) {
        return KnightLibModelSource.geo(Robots.of("geo/tank_mech.geo.json"));
    }

}