package dev.xylonity.bonsai.robots.client.projectile.renderer;

import dev.xylonity.bonsai.robots.common.entity.projectile.LaserProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LaserProjectileRenderer<T extends LaserProjectileEntity> extends AbstractProjectileRenderer<T> {

    public LaserProjectileRenderer(EntityRendererProvider.Context context) {
        this(context, "laser");
    }

    public LaserProjectileRenderer(EntityRendererProvider.Context context, String name) {
        super(context, name, null);
    }

}
