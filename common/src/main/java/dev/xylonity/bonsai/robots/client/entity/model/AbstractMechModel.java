package dev.xylonity.bonsai.robots.client.entity.model;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.api.util.KnightLibMath;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public abstract class AbstractMechModel<T extends AbstractMechEntity> extends GeoModel<T> {

    public static final String TORSO_BONE = "body_player_control";

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        final CoreGeoBone torso = getAnimationProcessor().getBone(TORSO_BONE);
        if (torso == null) {
            return;
        }

        final float offset = animatable.clientYawInitialized
                ? KnightLibMath.angleDelta(animatable.clientLegsYaw, animatable.clientTorsoYaw)
                : 0.0F;

        torso.setRotY(-KnightLibMath.toRadians(offset));
    }

}
