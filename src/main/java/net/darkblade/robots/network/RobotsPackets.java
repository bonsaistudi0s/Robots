package net.darkblade.robots.network;

import net.darkblade.robots.Robots;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class RobotsPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Robots.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        INSTANCE.messageBuilder(MechAttackC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MechAttackC2SPacket::new)
                .encoder(MechAttackC2SPacket::toBytes)
                .consumerMainThread(MechAttackC2SPacket::handle)
                .add();

        INSTANCE.messageBuilder(MechShootC2SPacket.class, id(), net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                .decoder(MechShootC2SPacket::new)
                .encoder(MechShootC2SPacket::toBytes)
                .consumerMainThread(MechShootC2SPacket::handle)
                .add();

        INSTANCE.messageBuilder(MechShootArrowC2SPacket.class, id(), net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                .decoder(MechShootArrowC2SPacket::new)
                .encoder(MechShootArrowC2SPacket::toBytes)
                .consumerMainThread(MechShootArrowC2SPacket::handle)
                .add();
    }
}