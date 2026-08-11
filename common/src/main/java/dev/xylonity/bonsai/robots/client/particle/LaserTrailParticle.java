package dev.xylonity.bonsai.robots.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class LaserTrailParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final float spin;
    private final Vec3 start;
    private final Vec3 destination;

    private LaserTrailParticle(ClientLevel level, double x, double y, double z, double travelX, double travelY, double travelZ, SpriteSet sprites, float minimumSize, float sizeVariation, int minimumLifetime, int lifetimeVariation, float red, float green, float blue) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.start = new Vec3(x, y, z);
        this.destination = this.start.add(travelX, travelY, travelZ);
        this.quadSize = minimumSize + this.random.nextFloat() * sizeVariation;
        this.lifetime = minimumLifetime + this.random.nextInt(lifetimeVariation + 1);
        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;
        this.hasPhysics = false;
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
        this.oRoll = this.roll;
        this.spin = (this.random.nextFloat() - 0.5F) * 0.14F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        final double progress = this.age / (double) this.lifetime;
        final double easedProgress = progress * progress * (3.0D - 2.0D * progress);
        final Vec3 next = this.start.lerp(this.destination, easedProgress);

        this.move(next.x - this.x, next.y - this.y, next.z - this.z);

        this.roll += this.spin;

        this.alpha = 1.0F - smoothstep(0.48F, 1.0F, (float) progress);
        this.setSpriteFromAge(this.sprites);
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        final float trim = Mth.clamp((value - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return trim * trim * (3.0F - 2.0F * trim);
    }

    @Override
    public float getQuadSize(float partialTick) {
        final float life = Mth.clamp((this.age + partialTick) / this.lifetime, 0.0F, 1.0F);
        return this.quadSize * (0.88F + 0.12F * Mth.sin(life * Mth.PI));
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;
        private final float minimumSize;
        private final float sizeVariation;
        private final int minimumLifetime;
        private final int lifetimeVariation;
        private final float red;
        private final float green;
        private final float blue;

        public Provider(SpriteSet sprites) {
            this(sprites, 0.045F, 0.125F, 12, 6, 1.0F, 0.4F, 0.4F);
        }

        public Provider(SpriteSet sprites, float minimumSize, float sizeVariation, int minimumLifetime, int lifetimeVariation) {
            this(sprites, minimumSize, sizeVariation, minimumLifetime, lifetimeVariation, 1.0F, 0.4F, 0.4F);
        }

        public Provider(SpriteSet sprites, float minimumSize, float sizeVariation, int minimumLifetime, int lifetimeVariation, float red, float green, float blue) {
            this.sprites = sprites;
            this.minimumSize = minimumSize;
            this.sizeVariation = sizeVariation;
            this.minimumLifetime = minimumLifetime;
            this.lifetimeVariation = lifetimeVariation;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double travelX, double travelY, double travelZ) {
            return new LaserTrailParticle(level, x, y, z, travelX, travelY, travelZ, this.sprites,
                    this.minimumSize, this.sizeVariation, this.minimumLifetime, this.lifetimeVariation,
                    this.red, this.green, this.blue);
        }

    }

}