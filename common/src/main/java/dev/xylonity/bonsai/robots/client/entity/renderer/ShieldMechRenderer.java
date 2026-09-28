package dev.xylonity.bonsai.robots.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.client.entity.layer.GenericMechElectricFieldLayer;
import dev.xylonity.bonsai.robots.client.entity.layer.GenericMechRiderLayer;
import dev.xylonity.bonsai.robots.client.render.ForceFieldSphere;
import dev.xylonity.bonsai.robots.client.render.MechForceFieldTextures;
import dev.xylonity.bonsai.robots.common.entity.mech.ShieldMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ShieldMechRenderer extends AbstractMechRenderer<ShieldMechEntity> {

    public static final ResourceLocation TEXTURE_LOCATION = Robots.of("textures/entity/shield_mech/shield_mech.png");
    public static final ResourceLocation GLOW_TEXTURE = Robots.of("textures/entity/shield_mech/shield_mech_glow.png");
    public static final ResourceLocation ELECTRIC_FIELD = Robots.of("textures/entity/shield_mech/shield_mech_glow_electric.png");

    private static final float SPHERE_RADIUS = 3.25F;

    public ShieldMechRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, 1.5f);
        addRenderLayer(new GenericMechRiderLayer<>());

        addEmissiveLayer(mech -> mech.isAbilityToggled(RobotsAbilities.ELECTRIC_FIELD) ? null : GLOW_TEXTURE);
        addRenderLayer(new GenericMechElectricFieldLayer<>(mech -> mech.isAbilityToggled(RobotsAbilities.ELECTRIC_FIELD) ? ELECTRIC_FIELD : null));
    }

    @Override
    protected void afterRender(ShieldMechEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.afterRender(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
        if (!entity.hasShieldSphere() || entity.isInvisible()) {
            return;
        }

        final ResourceLocation texture = MechForceFieldTextures.resolveSphere(entity.level().getGameTime());
        if (texture == null) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(0, 1.5f, 0);
        ForceFieldSphere.render(poseStack, buffers, texture, SPHERE_RADIUS);

        poseStack.popPose();
    }

    @Override
    protected double getCullingInflation(ShieldMechEntity entity) {
        return entity.hasShieldSphere() ? SPHERE_RADIUS : super.getCullingInflation(entity);
    }

    @Override
    protected boolean supportsLegIk() {
        return true;
    }

    @Override
    protected String getTorsoYawBone() {
        return "body";
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
                model.applyRotation(TORSO_BONE, entity.clientTorsoPitch, 0, 0);
            }

        }

    }

    @Override
    public ResourceLocation getTextureLocation(ShieldMechEntity entity) {
        return TEXTURE_LOCATION;
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
