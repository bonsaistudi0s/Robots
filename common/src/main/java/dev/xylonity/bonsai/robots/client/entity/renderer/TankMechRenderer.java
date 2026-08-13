package dev.xylonity.bonsai.robots.client.entity.renderer;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.client.entity.layer.GenericMechElectricFieldLayer;
import dev.xylonity.bonsai.robots.client.entity.layer.TankMechRiderLayer;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class TankMechRenderer extends AbstractMechRenderer<TankMechEntity> {

    private static final ResourceLocation ELECTRIC_FIELD = Robots.of("textures/entity/tank_mech/tank_mech_glow_electric.png");

    // Upper and lower leg bones are siblings, so both are repositioned independently
    private static final float THIGH_DROP_UNITS = 10f;
    private static final float THIGH_BACK_UNITS = 3f;
    private static final float MIN_THIGH_VERTICAL_UNITS = 2f;

    public TankMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, 1.5f);
        addRenderLayer(new TankMechRiderLayer());
        addEmissiveLayer(tankMechEntity -> Robots.of("textures/entity/tank_mech/tank_mech_glow.png"));
        addRenderLayer(new GenericMechElectricFieldLayer<>(mech -> mech.isAbilityToggled(RobotsAbilities.ELECTRIC_FIELD) ? ELECTRIC_FIELD : null));
    }

    @Override
    protected boolean supportsLegIk() {
        return true;
    }

    @Override
    protected void applyLegIk(TankMechEntity entity, KnightLibModel model) {
        if (!entity.clientLegIkInitialized || !hasLegIkBones(model)) {
            return;
        }

        final float left = entity.clientLeftLegGroundDelta;
        final float right = entity.clientRightLegGroundDelta;
        final float maxLiftBlocks = (THIGH_DROP_UNITS - MIN_THIGH_VERTICAL_UNITS) / 16f;
        final float letThatSinkIn = legIkBodySink(left, right, maxLiftBlocks);

        model.applyPosition(ROOT_BONE, 0f, letThatSinkIn * 16f, 0f);
        applyIK(model, LEFT_UPPER_LEG_BONE, LEFT_LOWER_LEG_BONE, (left - letThatSinkIn) * 16f);
        applyIK(model, RIGHT_UPPER_LEG_BONE, RIGHT_LOWER_LEG_BONE, (right - letThatSinkIn) * 16f);
    }

    @Override
    protected void setupPose(TankMechEntity entity, KnightLibModel model, float partialTick) {
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

    private void applyIK(KnightLibModel model, String upperBone, String lowerBone, float footLiftUnits) {
        final float thighLength = Mth.sqrt(THIGH_DROP_UNITS * THIGH_DROP_UNITS + THIGH_BACK_UNITS * THIGH_BACK_UNITS);
        final float lift = Mth.clamp(footLiftUnits, -2.5f, THIGH_DROP_UNITS - MIN_THIGH_VERTICAL_UNITS);

        final float kneeDrop = Mth.clamp(THIGH_DROP_UNITS - lift, MIN_THIGH_VERTICAL_UNITS, THIGH_DROP_UNITS);
        final float kneeBack = Mth.sqrt(thighLength * thighLength - kneeDrop * kneeDrop);
        final float kneeShift = -0.75f * (kneeBack - THIGH_BACK_UNITS);
        final float bend = (float) Math.toDegrees(Math.atan2(THIGH_BACK_UNITS + kneeShift, kneeDrop) - Math.atan2(THIGH_BACK_UNITS, THIGH_DROP_UNITS));

        model.applyRotation(upperBone, -bend, 0f, 0f);
        model.applyPosition(lowerBone, 0f, lift, kneeShift);
    }

    @Override
    public ResourceLocation getTextureLocation(TankMechEntity tankMechEntity) {
        return Robots.of("textures/entity/tank_mech/tank_mech.png");
    }

    @Override
    protected ResourceLocation getPaletteSprite(TankMechEntity entity) {
        return Robots.of("entity/tank_mech/tank_mech");
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
