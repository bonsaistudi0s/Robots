package dev.xylonity.bonsai.robots.mixin;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    // Hiding default hotbar
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void robots$hideHotbarWhilePiloting(float partialTick, GuiGraphics guiGraphics, CallbackInfo ci) {
        if (robots$isPilotingMech()) {
            ci.cancel();
        }

    }

    // Hiding mech health
    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void robots$hideVehicleHealthWhilePiloting(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (robots$isPilotingMech()) {
            ci.cancel();
        }

    }

    // Hiding player hearts
    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
    private void robots$hidePlayerHealthWhilePiloting(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (robots$isPilotingMech()) {
            ci.cancel();
        }

    }

    // Hiding exp bar
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void robots$hideExperienceWhilePiloting(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        if (robots$isPilotingMech()) {
            ci.cancel();
        }

    }

    // Renders the vanilla crosshair when controlling a mech
    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
    private boolean robots$showCrosshairWhilePiloting(CameraType cameraType) {
        final LocalPlayer player = Minecraft.getInstance().player;
        return cameraType.isFirstPerson() || player != null && player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player;
    }

    @Unique
    private static boolean robots$isPilotingMech() {
        final LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.getVehicle() instanceof AbstractMechEntity mech && mech.getControllingPassenger() == player;
    }

}