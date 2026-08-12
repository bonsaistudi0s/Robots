package dev.xylonity.bonsai.robots.client.entity.renderer;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.client.entity.layer.GenericMechElectricFieldLayer;
import dev.xylonity.bonsai.robots.common.entity.mech.TallMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TallMechRenderer extends AbstractMechRenderer<TallMechEntity> {

    private static final ResourceLocation ELECTRIC_FIELD = Robots.of("textures/entity/tall_mech/tall_mech_glow_electric.png");

    // The lower leg is a child of the upper leg, so moving the knee independently would apply the hip rotation twice
    private static final float UPPER_DROP_UNITS = 14.5f;
    private static final float UPPER_FORWARD_UNITS = 5.5f;
    private static final float LOWER_DROP_UNITS = 38.5f;
    private static final float LOWER_FORWARD_UNITS = 2.5f;
    private static final float MAX_FOOT_LIFT_UNITS = 16.0f;

    private static final float UPPER_LENGTH = Mth.sqrt(UPPER_DROP_UNITS * UPPER_DROP_UNITS + UPPER_FORWARD_UNITS * UPPER_FORWARD_UNITS);
    private static final float LOWER_LENGTH = Mth.sqrt(LOWER_DROP_UNITS * LOWER_DROP_UNITS + LOWER_FORWARD_UNITS * LOWER_FORWARD_UNITS);
    private static final float REST_UPPER_ANGLE = (float) Math.atan2(UPPER_FORWARD_UNITS, UPPER_DROP_UNITS);
    private static final float REST_LOWER_ANGLE = (float) Math.atan2(LOWER_FORWARD_UNITS, LOWER_DROP_UNITS) + (float) Math.toRadians(-5f);
    private static final float REST_FOOT_FORWARD = UPPER_LENGTH * Mth.sin(REST_UPPER_ANGLE) + LOWER_LENGTH * Mth.sin(REST_LOWER_ANGLE);
    private static final float REST_FOOT_DROP = UPPER_LENGTH * Mth.cos(REST_UPPER_ANGLE) + LOWER_LENGTH * Mth.cos(REST_LOWER_ANGLE);

    public TallMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, 1.5f);
        this.footLateralOffset = 11.0f / 16.0f;
        addRenderLayer(new GenericMechElectricFieldLayer<>(mech -> mech.isAbilityToggled(RobotsAbilities.ELECTRIC_FIELD) ? ELECTRIC_FIELD : null));
    }

    @Override
    protected boolean supportsLegIk() {
        return true;
    }

    @Override
    protected void applyLegIk(TallMechEntity entity, KnightLibModel model) {
        if (!entity.clientLegIkInitialized || !hasLegIkBones(model)) {
            return;
        }

        final float left = entity.clientLeftLegGroundDelta;
        final float right = entity.clientRightLegGroundDelta;
        final float sink = legIkBodySink(left, right, MAX_FOOT_LIFT_UNITS / 16f);

        model.applyPosition(ROOT_BONE, 0f, sink * 16f, 0f);
        applyIK(model, LEFT_UPPER_LEG_BONE, LEFT_LOWER_LEG_BONE, (left - sink) * 16f);
        applyIK(model, RIGHT_UPPER_LEG_BONE, RIGHT_LOWER_LEG_BONE, (right - sink) * 16f);
    }

    private void applyIK(KnightLibModel model, String upperBone, String lowerBone, float footLiftUnits) {
        final float lift = Mth.clamp(footLiftUnits, minimumReachableLift(), MAX_FOOT_LIFT_UNITS);
        final float targetDrop = REST_FOOT_DROP - lift;
        final float targetDistance = Mth.sqrt(REST_FOOT_FORWARD * REST_FOOT_FORWARD + targetDrop * targetDrop);
        final float targetAngle = (float) Math.atan2(REST_FOOT_FORWARD, targetDrop);

        final float cos = Mth.clamp((UPPER_LENGTH * UPPER_LENGTH + targetDistance * targetDistance - LOWER_LENGTH * LOWER_LENGTH) / (2f * UPPER_LENGTH * targetDistance), -1f, 1f);
        final float upperAngle = targetAngle + (float) Math.acos(cos);
        final float kneeForward = UPPER_LENGTH * Mth.sin(upperAngle);
        final float kneeDrop = UPPER_LENGTH * Mth.cos(upperAngle);
        final float lowerAngle = (float) Math.atan2(REST_FOOT_FORWARD - kneeForward, targetDrop - kneeDrop);

        final float upperDelta = (float) Math.toDegrees(upperAngle - REST_UPPER_ANGLE);
        final float lowerDelta = (float) Math.toDegrees((lowerAngle - upperAngle) - (REST_LOWER_ANGLE - REST_UPPER_ANGLE));

        model.applyRotation(upperBone, upperDelta, 0f, 0f);
        model.applyRotation(lowerBone, lowerDelta, 0f, 0f);
    }

    private float minimumReachableLift() {
        final float maximumReach = UPPER_LENGTH + LOWER_LENGTH - 1.0E-3f;
        final float maximumDrop = Mth.sqrt(maximumReach * maximumReach - REST_FOOT_FORWARD * REST_FOOT_FORWARD);
        return REST_FOOT_DROP - maximumDrop;
    }

    @Override
    public ResourceLocation getTextureLocation(TallMechEntity tankMechEntity) {
        return Robots.of("textures/entity/tall_mech/tall_mech_green.png");
    }

    @Override
    protected KnightLibAnimationSource defineAnimations(TallMechEntity entity) {
        return KnightLibAnimationSource.geo(Robots.of("animations/tall_mech.animation.json"));
    }

    @Override
    protected KnightLibModelSource defineModel(TallMechEntity tankMechEntity) {
        return KnightLibModelSource.geo(Robots.of("geo/tall_mech.geo.json"));
    }

}