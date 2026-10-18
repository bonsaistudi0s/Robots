package dev.xylonity.bonsai.robots.client.sound;

import dev.xylonity.bonsai.robots.common.entity.projectile.RocketProjectileEntity;
import dev.xylonity.bonsai.robots.registry.RobotsSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class RocketFlameSoundInstance extends AbstractTickableSoundInstance {

    private final RocketProjectileEntity rocket;
    private int fadeOutTicks;

    public RocketFlameSoundInstance(RocketProjectileEntity rocket) {
        super(RobotsSounds.FLAME.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.rocket = rocket;
        this.volume = 0.5F;
        this.pitch = 0.9F + this.random.nextFloat() * 0.2F;
        this.x = rocket.getX();
        this.y = rocket.getY();
        this.z = rocket.getZ();
    }

    @Override
    public void tick() {
        if (!this.rocket.isRemoved()) {
            this.x = this.rocket.getX();
            this.y = this.rocket.getY();
            this.z = this.rocket.getZ();
            return;
        }

        if (++this.fadeOutTicks >= 4) {
            this.stop();
            return;
        }

        this.volume = 0.5F * (1.0F - this.fadeOutTicks / (float) 4);
    }

}