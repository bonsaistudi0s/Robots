package dev.xylonity.bonsai.robots.mixin;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    // Not showing the default hotbar when the player is controlling a mech
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void robots$hideHotbarWhilePiloting(float partialTick, GuiGraphics guiGraphics, CallbackInfo ci) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player) {
            ci.cancel();
        }

    }

    // The vanilla jump-charge meter would overlap the mech energy bar; the charge is drawn by MechHudOverlay instead
    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
    private void robots$hideJumpMeterWhilePiloting(net.minecraft.world.entity.PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player) {
            ci.cancel();
        }

    }

    // Renders the vanilla crosshair when controlling a mech
    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
    private boolean robots$showCrosshairWhilePiloting(CameraType cameraType) {
        final LocalPlayer player = Minecraft.getInstance().player;
        return cameraType.isFirstPerson() || player != null && player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player;
    }

}
