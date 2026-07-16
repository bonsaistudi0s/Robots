package dev.xylonity.bonsai.robots.client.util;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityManager;
import dev.xylonity.bonsai.robots.network.packets.StartAimingC2SPacket;
import dev.xylonity.bonsai.robots.network.packets.UseAbilityC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public class RobotsClientUtil {

    public static boolean handleMouseAbility(int slot) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return false;
        }

        final AbstractMechEntity mech = pilotedMech(minecraft.player);
        if (mech == null) {
            return false;
        }

        final int selected = mech.getSelectedSlot();
        if (selected >= 0) {
            if (slot == AbilityManager.SECONDARY_SLOT) {
                Robots.NETWORK.sendToServer(new StartAimingC2SPacket(selected));
            }
            else if (slot == AbilityManager.PRIMARY_SLOT) {
                Robots.NETWORK.sendToServer(new UseAbilityC2SPacket(selected));
            }

        }
        else if (mech.getAbilityManager().get(slot) != null) {
            Robots.NETWORK.sendToServer(new UseAbilityC2SPacket(slot));
        }

        return true;
    }

    @Nullable
    public static AbstractMechEntity pilotedMech(LocalPlayer player) {
        return player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player ? mech : null;
    }

}