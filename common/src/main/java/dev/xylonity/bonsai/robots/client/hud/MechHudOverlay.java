package dev.xylonity.bonsai.robots.client.hud;

import dev.xylonity.bonsai.robots.Robots;
import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.common.entity.ability.AbilityManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Not a screen per se, there isn't really need to use one at all here
 */
public final class MechHudOverlay {

    private static final ResourceLocation UI = Robots.of("textures/entity/mech_ui.png");

    private static final int ICON_SIZE = 16;
    private static final int SLOT_BORDER_SIZE = 18;
    private static final int SLOT_FRAME = 20;
    private static final int SELECTED_FRAME_SIZE = 20;

    private static final int HEART_WIDTH = 22;
    private static final int HEART_HEIGHT = 19;
    private static final int HEART_FILL_WIDTH = 14;
    private static final int HEART_FILL_HEIGHT = 10;

    private static final int ENERGY_BAR_WIDTH = 122;
    private static final int ENERGY_BAR_HEIGHT = 3;

    private static final int PLAYER_BAR_WIDTH = 51;
    private static final int PLAYER_BAR_HEIGHT = 7;

    private static final int BASICS_GAP = 2;

    public static void render(GuiGraphics graphics, float partialTick) {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }
        if (!(player.getVehicle() instanceof AbstractMechEntity mech) || mech.getControllingPassenger() != player) {
            return;
        }

        final int width = graphics.guiWidth();
        final int height = graphics.guiHeight();
        final AbilityManager abilities = mech.getAbilityManager();

        final int specials = abilities.specialSlotCount();
        final boolean hasPrimary = abilities.get(AbilityManager.PRIMARY_SLOT) != null;
        final boolean hasSecondary = abilities.get(AbilityManager.SECONDARY_SLOT) != null;
        final int simples = (hasPrimary ? 1 : 0) + (hasSecondary ? 1 : 0);

        // Slot selection frame additional separation
        final int specialsWidth = specials * SLOT_FRAME;
        final int basicsWidth = simples > 0 ? (simples - 1) * SLOT_FRAME + SLOT_BORDER_SIZE : 0;
        final int rowWidth = specialsWidth + HEART_WIDTH + (simples > 0 ? BASICS_GAP + basicsWidth : 0);

        final int rowX = width / 2 - rowWidth / 2;
        final int rowY = height - HEART_HEIGHT - 4;

        // specials
        for (int i = 0; i < specials; i++) {
            drawSlot(graphics, mech, i, rowX + i * SLOT_FRAME, rowY);
        }

        // Heart
        final int heartX = rowX + specialsWidth;
        graphics.blit(UI, heartX, rowY, 60, 0, HEART_WIDTH, HEART_HEIGHT);

        // Culling for the actual heart hp asset
        final float health = Mth.clamp(mech.getHealth() / mech.getMaxHealth(), 0.0F, 1.0F);
        final int fill = health <= 0.0F ? 0 : Math.max(1, Math.round(HEART_FILL_HEIGHT * health));
        if (fill > 0) {
            final int cut = HEART_FILL_HEIGHT - fill;
            graphics.blit(UI, heartX + 4, rowY + 4 + cut, 64, 23 + cut, HEART_FILL_WIDTH, fill);
        }

        // Basic attacks
        int basicsX = heartX + HEART_WIDTH + BASICS_GAP;
        if (hasPrimary) {
            drawSlot(graphics, mech, AbilityManager.PRIMARY_SLOT, basicsX, rowY);
            basicsX += SLOT_FRAME;
        }
        if (hasSecondary) {
            drawSlot(graphics, mech, AbilityManager.SECONDARY_SLOT, basicsX, rowY);
        }

        // Energy bar
        final int energyX = width / 2 - ENERGY_BAR_WIDTH / 2;
        final int energyY = rowY - ENERGY_BAR_HEIGHT - 3;
        graphics.blit(UI, energyX, energyY, 0, 38, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);

        final int energyWidth = fillWidth(mech.getEnergy() / mech.getMaxEnergy(), ENERGY_BAR_WIDTH);
        if (energyWidth > 0) {
            graphics.blit(UI, energyX, energyY, 0, 41, energyWidth, ENERGY_BAR_HEIGHT);
        }

        // Player health and hunger bars are render above the energy bar
        final int playerBarsY = energyY - PLAYER_BAR_HEIGHT - 1;
        drawPlayerBar(graphics, energyX, playerBarsY, player.getHealth() / player.getMaxHealth(), 50);
        drawPlayerBar(graphics, energyX + ENERGY_BAR_WIDTH - PLAYER_BAR_WIDTH, playerBarsY, player.getFoodData().getFoodLevel() / 20.0F, 55);
    }

    private static void drawSlot(GuiGraphics graphics, AbstractMechEntity mech, int slot, int x, int y) {
        final AbilityManager abilities = mech.getAbilityManager();

        graphics.blit(UI, x, y, 0, 0, SLOT_BORDER_SIZE, SLOT_BORDER_SIZE);

        final ResourceLocation id = abilities.getId(slot);
        if (id != null) {
            graphics.blit(mech.getAbilityIcon(id), x + 1, y + 1, 0, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        final float cooldown = abilities.getCooldownProgress(slot);
        if (cooldown > 0.0F) {
            final int overlay = Math.round(ICON_SIZE * cooldown);
            graphics.fill(x + 1, y + 1 + ICON_SIZE - overlay, x + 1 + ICON_SIZE, y + 1 + ICON_SIZE, 0x88FFFFFF);
        }

        // Basic abilities are permanent, not selections per se, so they shouldn't use the selection frame
        // Renders the selection frame
        if (mech.getSelectedSlot() == slot || mech.isToggleActive(slot)) {
            graphics.blit(UI, x - 1, y - 1, 0, 59, SELECTED_FRAME_SIZE, SELECTED_FRAME_SIZE);
        }

    }

    private static void drawPlayerBar(GuiGraphics graphics, int x, int y, float progress, int fillV) {
        graphics.blit(UI, x, y, PLAYER_BAR_WIDTH, PLAYER_BAR_HEIGHT, 0, 44, PLAYER_BAR_WIDTH, 5, 256, 256);

        final int filled = fillWidth(progress, 49);
        if (filled > 0) {
            graphics.blit(UI, x + 1, y + 1, filled, 5, 1, fillV, filled, 3, 256, 256);
        }
    }

    private static int fillWidth(float progress, int width) {
        final float clamped = Mth.clamp(progress, 0.0F, 1.0F);
        return clamped <= 0.0F ? 0 : Math.max(1, Math.round(width * clamped));
    }

}