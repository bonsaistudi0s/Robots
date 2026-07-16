package dev.xylonity.bonsai.robots.client.network;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.network.packets.AbilityCooldownS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientPacketHandler {

    public static void handleAbilityCooldown(AbilityCooldownS2CPacket pkt) {
        final ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getEntity(pkt.entityId()) instanceof AbstractMechEntity mech) {
            mech.getAbilityManager().setCooldownClient(pkt.slot(), pkt.cooldown());
        }

    }

}
