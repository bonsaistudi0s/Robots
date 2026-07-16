package dev.xylonity.bonsai.robots.mixin;

import dev.xylonity.bonsai.robots.client.util.RobotsClientUtil;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables the player main controls when it's controlling a mech
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    // Left click
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void robots$primaryAbilityOnAttack(CallbackInfoReturnable<Boolean> cir) {
        if (RobotsClientUtil.handleMouseAbility(AbilityManager.PRIMARY_SLOT)) {
            cir.setReturnValue(false);
        }

    }

    // Right click
    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void robots$secondaryAbilityOnUse(CallbackInfo ci) {
        if (RobotsClientUtil.handleMouseAbility(AbilityManager.SECONDARY_SLOT)) {
            ci.cancel();
        }

    }

    // Hold left click
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void robots$noMiningWhilePiloting(boolean leftClick, CallbackInfo ci) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (leftClick && minecraft.player != null && RobotsClientUtil.pilotedMech(minecraft.player) != null) {
            ci.cancel();
        }

    }

}
