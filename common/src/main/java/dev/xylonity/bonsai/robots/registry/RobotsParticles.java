package dev.xylonity.bonsai.robots.registry;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class RobotsParticles {

    public static final ResourceRegistry<ParticleType<?>> PARTICLES = ResourceDispatcher.create(BuiltInRegistries.PARTICLE_TYPE, Robots.MOD_ID);

    public static final ResourceEntry<SimpleParticleType> LASER_TRAIL = PARTICLES.register("laser_trail", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> SMALL_LASER_TRAIL = PARTICLES.register("small_laser_trail", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> ELECTRIC_LASER_TRAIL = PARTICLES.register("electric_laser_trail", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> ELECTRIC_SMALL_LASER_TRAIL = PARTICLES.register("electric_small_laser_trail", KnightLib.PLATFORM.createParticle(true));

}