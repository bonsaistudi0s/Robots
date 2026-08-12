package dev.xylonity.bonsai.robots.client.event;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Robots.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class RobotsForgeClientEvents {

    /**
     * Forge renders the gui separately, so the GuiMixin won't work
     */
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !(player.getVehicle() instanceof AbstractMechEntity mech) || mech.getControllingPassenger() != player) {
            return;
        }

        final ResourceLocation overlay = event.getOverlay().id();
        if (
                overlay.equals(VanillaGuiOverlay.PLAYER_HEALTH.id()) ||
                overlay.equals(VanillaGuiOverlay.ARMOR_LEVEL.id()) ||
                overlay.equals(VanillaGuiOverlay.FOOD_LEVEL.id()) ||
                overlay.equals(VanillaGuiOverlay.AIR_LEVEL.id()) ||
                overlay.equals(VanillaGuiOverlay.MOUNT_HEALTH.id()) ||
                overlay.equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())
        ) {
            event.setCanceled(true);
        }

    }

}