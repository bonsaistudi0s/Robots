package dev.xylonity.bonsai.robots.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayer;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public class TankMechRiderLayer extends KnightLibRenderLayer<TankMechEntity> {

    @Override
    public boolean shouldRender(KnightLibRenderLayerContext<TankMechEntity> context) {
        return context.target().isVehicle();
    }

    @Override
    public void render(final KnightLibRenderLayerContext<TankMechEntity> context) {
        final Minecraft minecraft = Minecraft.getInstance();
        final TankMechEntity mech = context.target();
        final PoseStack poseStack = context.poseStack();
        final float partialTick = context.partialTick();

        final float bodyYaw = Mth.rotLerp(partialTick, mech.yBodyRotO, mech.yBodyRot);

        context.visitBones(Set.of("player"), (name, pose, normal) -> {
            for (final Entity passenger : mech.getPassengers()) {
                if (passenger == minecraft.player && minecraft.options.getCameraType().isFirstPerson()) {
                    continue;
                }

                final EntityRenderer<? super Entity> renderer = minecraft.getEntityRenderDispatcher().getRenderer(passenger);
                if (renderer == null) {
                    continue;
                }

                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(bodyYaw + 180));
                poseStack.translate(0, -0.7, 0);
                renderer.render(passenger, 0f, partialTick, poseStack, context.buffers(), context.packedLight());
                poseStack.popPose();
            }

        });

    }

}