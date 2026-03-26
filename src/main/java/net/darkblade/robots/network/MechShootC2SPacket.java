package net.darkblade.robots.network;

import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MechShootC2SPacket {
    public MechShootC2SPacket() {}
    public MechShootC2SPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.getVehicle() instanceof TankMechEntity mech) {
                mech.triggerManualShoot();
            }
        });
        return true;
    }
}