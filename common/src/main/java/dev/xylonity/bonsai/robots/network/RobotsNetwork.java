package dev.xylonity.bonsai.robots.network;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.network.packets.AbilityCooldownS2CPacket;
import dev.xylonity.bonsai.robots.network.packets.MechSprintC2SPacket;
import dev.xylonity.bonsai.robots.network.packets.SelectAbilityC2SPacket;
import dev.xylonity.bonsai.robots.network.packets.StartAimingC2SPacket;
import dev.xylonity.bonsai.robots.network.packets.UseAbilityC2SPacket;

public final class RobotsNetwork {

    public static void register() {
        Robots.NETWORK.register(UseAbilityC2SPacket.TYPE);
        Robots.NETWORK.register(SelectAbilityC2SPacket.TYPE);
        Robots.NETWORK.register(StartAimingC2SPacket.TYPE);
        Robots.NETWORK.register(MechSprintC2SPacket.TYPE);
        Robots.NETWORK.register(AbilityCooldownS2CPacket.TYPE);
    }

}
