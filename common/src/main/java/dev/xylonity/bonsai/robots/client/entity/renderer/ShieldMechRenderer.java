package dev.xylonity.bonsai.robots.client.entity.renderer;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.mech.ShieldMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ShieldMechRenderer extends AbstractMechRenderer<ShieldMechEntity> {

    private static final ResourceLocation ELECTRIC_FIELD = Robots.of("textures/entity/shield_mech/shield_mech_glow_electric.png");
    public ShieldMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, 1.5f);
        //addRenderLayer(new TankMechRiderLayer());
        addEmissiveLayer(mech -> mech.isAbilityToggled(RobotsAbilities.ELECTRIC_FIELD) ? null : Robots.of("textures/entity/shield_mech/shield_mech_glow.png"));
        //addRenderLayer(new GenericMechElectricFieldLayer<>(mech -> mech.isAbilityToggled(RobotsAbilities.ELECTRIC_FIELD) ? ELECTRIC_FIELD : null));
    }

    @Override
    protected boolean supportsLegIk() {
        return true;
    }

    @Override
    protected void setupPose(ShieldMechEntity entity, KnightLibModel model, float partialTick) {
        super.setupPose(entity, model, partialTick);

        // Saves the latest pitch rotation after the player unmounts so the mech keeps looking to that direction
        if (model.hasBone(TORSO_BONE)) {
            final Player pilot = entity.getPilot();
            if (pilot != null) {
                entity.clientTorsoPitch = Mth.clamp(pilot.getViewXRot(partialTick), -22.5F, 22.5F);
                entity.clientPitchInitialized = true;
            }
            if (entity.clientPitchInitialized) {
                model.applyRotation(TORSO_BONE, entity.clientTorsoPitch, 0.0F, 0.0F);
            }

        }

    }

    @Override
    public ResourceLocation getTextureLocation(ShieldMechEntity entity) {
        return Robots.of("textures/entity/shield_mech/shield_mech.png");
    }

    @Override
    protected ResourceLocation getPaletteSprite(ShieldMechEntity entity) {
        return Robots.of("entity/shield_mech/shield_mech");
    }

    @Override
    protected KnightLibAnimationSource defineAnimations(ShieldMechEntity entity) {
        return KnightLibAnimationSource.geo(Robots.of("animations/shield_mech.animation.json"));
    }

    @Override
    protected KnightLibModelSource defineModel(ShieldMechEntity entity) {
        return KnightLibModelSource.geo(Robots.of("geo/shield_mech.geo.json"));
    }

}
