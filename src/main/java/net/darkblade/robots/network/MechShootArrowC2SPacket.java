package net.darkblade.robots.network;

import net.darkblade.robots.entity.TankMechEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class MechShootArrowC2SPacket {
    private final double x, y, z;

    public MechShootArrowC2SPacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Constructor que lee el servidor
    public MechShootArrowC2SPacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    // Escribimos los datos en el paquete
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.getVehicle() instanceof TankMechEntity mech) {
                mech.triggerManualArrow(new Vec3(this.x, this.y, this.z));
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}