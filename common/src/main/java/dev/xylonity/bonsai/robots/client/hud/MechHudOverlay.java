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
 * Not an screen per se, there isn't really need to use one at all here
 */
public final class MechHudOverlay {

    private static final ResourceLocation UI = Robots.of("textures/entity/mech_ui.png");

    private static final int SLOT_SIZE = 16;
    private static final int SLOT_STRIDE = 17;

    private static final int HEART_WIDTH = 20;
    private static final int HEART_FILL_HEIGHT = 10;

    private static final int BAR_WIDTH = 104;
    private static final int BAR_HEIGHT = 3;

    // The code is also expected to change slightly, because some mech may not have exactly 5 attack slots

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

        // Specials -> heart -> 1 pixel offset -> quick attacks
        final int specialsWidth = specials > 0 ? specials * SLOT_STRIDE - 1 : 0;
        final int simplesWidth = simples > 0 ? simples * SLOT_STRIDE - 1 : 0;
        final int rowWidth = specialsWidth + HEART_WIDTH + (simples > 0 ? 1 + simplesWidth : 0);

        final int rowX = width / 2 - rowWidth / 2;
        final int rowY = height - SLOT_SIZE - 4;

        // specials
        for (int i = 0; i < specials; i++) {
            drawSlot(graphics, mech, i, rowX + i * SLOT_STRIDE, rowY);
        }

        // Heart
        final int heartX = rowX + specialsWidth;
        graphics.blit(UI, heartX, rowY, 50, 0, HEART_WIDTH, 16);

        // Culling for the actual heart hp asset
        final float health = Mth.clamp(mech.getHealth() / mech.getMaxHealth(), 0.0F, 1.0F);
        final int fill = health <= 0.0F ? 0 : Math.max(1, Math.round(HEART_FILL_HEIGHT * health));
        if (fill > 0) {
            final int cut = HEART_FILL_HEIGHT - fill;
            graphics.blit(UI, heartX + 3, rowY + 3 + cut, 53, 19 + cut, 14, fill);
        }

        // simples
        int simplesX = heartX + HEART_WIDTH + 1;
        if (hasPrimary) {
            drawSlot(graphics, mech, AbilityManager.PRIMARY_SLOT, simplesX, rowY);
            simplesX += SLOT_STRIDE;
        }
        if (hasSecondary) {
            drawSlot(graphics, mech, AbilityManager.SECONDARY_SLOT, simplesX, rowY);
        }

        // Energy bar on top
        final int barX = width / 2 - BAR_WIDTH / 2;
        final int barY = rowY - BAR_HEIGHT - 3;
        graphics.blit(UI, barX, barY, 0, 32, BAR_WIDTH, BAR_HEIGHT);

        // Culling for the actual energy bar progression
        final int energyWidth = Math.round(BAR_WIDTH * Mth.clamp(mech.getEnergy() / mech.getMaxEnergy(), 0.0F, 1.0F));
        if (energyWidth > 0) {
            graphics.blit(UI, barX, barY, 0, 35, energyWidth, BAR_HEIGHT);
        }

        // Jump charge (replaces the vanilla jump meter, hidden via GuiMixin)
        final float jumpCharge = player.getJumpRidingScale();
        if (jumpCharge > 0.0F) {
            final int chargeY = barY - 3;
            graphics.fill(barX, chargeY, barX + BAR_WIDTH, chargeY + 2, 0x66000000);
            final int chargeWidth = Math.round(BAR_WIDTH * Mth.clamp(jumpCharge, 0.0F, 1.0F));
            if (chargeWidth > 0) {
                graphics.fill(barX, chargeY, barX + chargeWidth, chargeY + 2, 0xFFFFD84D);
            }

        }

    }

    private static void drawSlot(GuiGraphics graphics, AbstractMechEntity mech, int slot, int x, int y) {
        final AbilityManager abilities = mech.getAbilityManager();

        graphics.blit(UI, x, y, 0, 0, SLOT_SIZE, SLOT_SIZE);

        final ResourceLocation id = abilities.getId(slot);
        if (id != null) {
            graphics.blit(mech.getAbilityIcon(id), x, y, 0, 0.0F, 0.0F, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
        }

        if (mech.getSelectedSlot() == slot) {
            graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x66000000);
        }

        final float cooldown = abilities.getCooldownProgress(slot);
        if (cooldown > 0.0F) {
            final int overlay = Math.round(SLOT_SIZE * cooldown);
            graphics.fill(x, y + SLOT_SIZE - overlay, x + SLOT_SIZE, y + SLOT_SIZE, 0x88FFFFFF);
        }

    }

}