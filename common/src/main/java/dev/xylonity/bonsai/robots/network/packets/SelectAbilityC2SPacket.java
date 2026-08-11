package dev.xylonity.bonsai.robots.network.packets;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import dev.xylonity.knightlib.network.ServerboundPacketType;
import net.minecraft.server.level.ServerPlayer;

/**
 * Switches the current ability without activating it
 */
public record SelectAbilityC2SPacket(
        int slot
) {

    public static final ServerboundPacketType<SelectAbilityC2SPacket> TYPE = PacketType.serverbound(
            Robots.of("select_ability"),
            SelectAbilityC2SPacket.class,
            PacketCodec.of(
                    (packet, buf) -> buf.writeVarInt(packet.slot()),
                    buf -> new SelectAbilityC2SPacket(buf.readVarInt())),
            SelectAbilityC2SPacket::handle);

    private static void handle(SelectAbilityC2SPacket pkt, ServerPlayer player) {
        if (player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player) {
            mech.getAbilityManager().selectAbilityGroup(pkt.slot(), player);
        }

    }

}
