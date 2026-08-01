package dev.xylonity.bonsai.robots.client.projectile.renderer;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.knightlib.api.animation.KnightLibAnimatable;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.knightlib.client.animation.renderer.KnightLibEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractProjectileRenderer<T extends Entity & KnightLibAnimatable> extends KnightLibEntityRenderer<T> {

    private final ResourceLocation model;
    private final ResourceLocation texture;

    protected AbstractProjectileRenderer(EntityRendererProvider.Context context, String name, @Nullable String glowTexture) {
        super(context);
        this.model = Robots.of("geo/" + name + ".geo.json");
        this.texture = Robots.of("textures/entity/projectile/" + name + ".png");
        if (glowTexture != null) {
            final ResourceLocation glow = Robots.of("textures/entity/projectile/" + glowTexture + ".png");
            addEmissiveLayer(entity -> glow);
        }

        this.shadowRadius = 0.0F;
    }

    @Override
    protected KnightLibModelSource defineModel(T projectile) {
        return KnightLibModelSource.geo(this.model);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T projectile) {
        return this.texture;
    }

    @Override
    protected void setupPose(T projectile, KnightLibModel model, float partialTicks) {
        final float pitch = Mth.lerp(partialTicks, projectile.xRotO, projectile.getXRot());
        model.applyRotation("main", -pitch, 0.0F, 0.0F);
    }

}
