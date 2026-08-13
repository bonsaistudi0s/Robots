package dev.xylonity.bonsai.robots.client.projectile.renderer;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.registry.RobotsRenderTypes;
import dev.xylonity.bonsai.robots.common.entity.projectile.LaserProjectileEntity;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.knightlib.registry.KnightLibRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GenericLaserProjectileRenderer<T extends LaserProjectileEntity> extends AbstractProjectileRenderer<T> {

    private final ResourceLocation electricTexture;
    private final float scale;

    public GenericLaserProjectileRenderer(EntityRendererProvider.Context context, String name, float scale) {
        super(context, name, null);
        this.electricTexture = Robots.of("textures/entity/projectile/" + name + "_electric.png");
        this.scale = scale;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T laser) {
        return laser.isElectric() ? this.electricTexture : super.getTextureLocation(laser);
    }

    @Override
    protected RenderType getRenderType(T laser, ResourceLocation texture) {
        return KnightLibRenderTypes.entityUnshadedEmissive(texture, true);
    }

    @Override
    protected float getScale(T laser) {
        return this.scale;
    }

    @Override
    protected void setupPose(T laser, KnightLibModel model, float partialTicks) {
        super.setupPose(laser, model, partialTicks);
        model.applyScale("main", 1.0F, 1.0F, 2.0F);
    }

}
