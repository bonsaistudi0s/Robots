package dev.xylonity.bonsai.robots.network.packets;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.client.network.ClientPacketHandler;
import dev.xylonity.knightlib.network.ClientboundPacketType;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;

/**
 * Syncs the cooldown to the hud
 */
public record AbilityCooldownS2CPacket(
        int entityId,
        int slot,
        int cooldown
) {

    public static final ClientboundPacketType<AbilityCooldownS2CPacket> TYPE = PacketType.clientbound(
            Robots.of("ability_cooldown"),
            AbilityCooldownS2CPacket.class,
            PacketCodec.of(
                    (packet, buf) -> {
                        buf.writeVarInt(packet.entityId());
                        buf.writeVarInt(packet.slot());
                        buf.writeVarInt(packet.cooldown());
                    },
                    buf -> new AbilityCooldownS2CPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt())),
            // Synthetic method don't change it lol
            pkt -> ClientPacketHandler.handleAbilityCooldown(pkt));

}