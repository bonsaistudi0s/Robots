package dev.xylonity.bonsai.robots.client.event;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.client.projectile.model.LaserProjectileModel;
import dev.xylonity.bonsai.robots.client.projectile.renderer.LaserProjectileRenderer;
import dev.xylonity.bonsai.robots.client.entity.renderer.TallMechRenderer;
import dev.xylonity.bonsai.robots.client.entity.renderer.TankMechRenderer;
import dev.xylonity.bonsai.robots.client.hud.MechHudOverlay;
import dev.xylonity.bonsai.robots.client.util.RobotsClientUtil;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityManager;
import dev.xylonity.bonsai.robots.network.packets.SelectAbilityC2SPacket;
import dev.xylonity.bonsai.robots.network.packets.StartAimingC2SPacket;
import dev.xylonity.bonsai.robots.network.packets.UseAbilityC2SPacket;
import dev.xylonity.bonsai.robots.registry.RobotsEntities;
import dev.xylonity.knightlib.api.event.RegisterEvent;
import dev.xylonity.knightlib.api.event.impl.client.*;
import dev.xylonity.knightlib.api.event.impl.interop.TickPhase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class RobotsClientEvents {

    private static int lockedHotbarSlot = -1;

    @RegisterEvent
    public static void registerEntityRenderers(final EntityRendererRegistrationEvent event) {
        event.register(RobotsEntities.TANK_MECH, TankMechRenderer::new);
        event.register(RobotsEntities.TALL_MECH, TallMechRenderer::new);
        event.register(RobotsEntities.LASER_PROJECTILE, LaserProjectileRenderer::new);
        event.register(RobotsEntities.BIG_LASER_PROJECTILE, ctx -> new LaserProjectileRenderer<>(ctx, new LaserProjectileModel<>("big_laser")));
    }

    // Ability selection (1 -> 9 mapping key selection)
    @RegisterEvent
    public static void onKeyInput(final ClientKeyInputEvent event) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null || event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }

        final AbstractMechEntity mech = RobotsClientUtil.pilotedMech(minecraft.player);
        if (mech == null) {
            return;
        }

        for (int i = 0; i < AbilityManager.MAX_SPECIAL_SLOTS; i++) {
            if (minecraft.options.keyHotbarSlots[i].matches(event.getKey(), event.getScanCode())) {
                if (!mech.getAbilityManager().isOnCooldown(i)) {
                    Robots.NETWORK.sendToServer(new SelectAbilityC2SPacket(i));
                }

                return;
            }

        }

    }

    // Slot selection cancel (as the hotbar cannot really cancel the selection)
    @RegisterEvent
    public static void onPlayerTick(final ClientPlayerTickEvent event) {
        if (event.getPhase() != TickPhase.START) {
            return;
        }

        final LocalPlayer player = event.getPlayer();
        if (RobotsClientUtil.pilotedMech(player) != null) {
            if (lockedHotbarSlot == -1) {
                lockedHotbarSlot = player.getInventory().selected;
            }

            player.getInventory().selected = lockedHotbarSlot;
        }
        else {
            lockedHotbarSlot = -1;
        }

    }

    // Disables the scroll wheel when piloting
    @RegisterEvent
    public static void onMouseScroll(final ClientMouseScrollEvent event) {
        final LocalPlayer player = event.getClient().player;
        if (player != null && event.getClient().screen == null && RobotsClientUtil.pilotedMech(player) != null) {
            event.setCancelled(true);
        }

    }

    @RegisterEvent
    public static void onRenderGui(final ClientRenderGuiEvent event) {
        MechHudOverlay.render(event.guiGraphics(), event.partialTick());
    }

}