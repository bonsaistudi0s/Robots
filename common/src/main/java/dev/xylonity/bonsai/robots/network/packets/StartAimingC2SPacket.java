package dev.xylonity.bonsai.robots.network.packets;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import dev.xylonity.knightlib.network.ServerboundPacketType;
import net.minecraft.server.level.ServerPlayer;

/**
 * Right click to aim over a selected special ability that requires it
 */
public record StartAimingC2SPacket(
        int slot
) {

    public static final ServerboundPacketType<StartAimingC2SPacket> TYPE = PacketType.serverbound(
            Robots.of("start_aiming"),
            StartAimingC2SPacket.class,
            PacketCodec.of(
                    (packet, buf) -> buf.writeVarInt(packet.slot()),
                    buf -> new StartAimingC2SPacket(buf.readVarInt())),
            StartAimingC2SPacket::handle);

    private static void handle(StartAimingC2SPacket pkt, ServerPlayer player) {
        if (player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player) {
            mech.getAbilityManager().startAiming(pkt.slot(), player);
        }
    }

}
