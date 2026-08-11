package dev.xylonity.bonsai.robots.network.packets;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import dev.xylonity.knightlib.network.ServerboundPacketType;
import net.minecraft.server.level.ServerPlayer;

/**
 * Keeps the mech's running state in sync with the player
 */
public record MechSprintC2SPacket(
        boolean sprinting
) {

    public static final ServerboundPacketType<MechSprintC2SPacket> TYPE = PacketType.serverbound(
            Robots.of("mech_sprint"),
            MechSprintC2SPacket.class,
            PacketCodec.of(
                    (packet, buffer) -> buffer.writeBoolean(packet.sprinting()),
                    buffer -> new MechSprintC2SPacket(buffer.readBoolean())),
            MechSprintC2SPacket::handle);

    private static void handle(MechSprintC2SPacket packet, ServerPlayer player) {
        if (player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player) {
            mech.setSprinting(mech.canSprint() && packet.sprinting());
        }

    }

}