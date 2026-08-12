package dev.xylonity.bonsai.robots.network.packets;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import dev.xylonity.knightlib.network.ServerboundPacketType;
import net.minecraft.server.level.ServerPlayer;

/**
 * Requests use of a basic ability or the currently selected special ability
 */
public record UseAbilityC2SPacket(
        int slot
) {

    public static final ServerboundPacketType<UseAbilityC2SPacket> TYPE = PacketType.serverbound(
            Robots.of("use_ability"),
            UseAbilityC2SPacket.class,
            PacketCodec.of(
                    (packet, buf) -> buf.writeVarInt(packet.slot()),
                    buf -> new UseAbilityC2SPacket(buf.readVarInt())),
            UseAbilityC2SPacket::handle);

    private static void handle(UseAbilityC2SPacket packet, ServerPlayer player) {
        if (player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player) {
            mech.getAbilityManager().tryUse(packet.slot(), player);
        }

    }

}
