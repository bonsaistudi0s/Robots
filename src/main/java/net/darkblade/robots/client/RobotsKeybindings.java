package net.darkblade.robots.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class RobotsKeybindings {
    public static final String CATEGORY = "key.category.robots";

    public static final KeyMapping MECH_ATTACK = new KeyMapping(
            "key.robots.mech_attack",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    );

    public static final KeyMapping MECH_SHOOT = new KeyMapping(
            "key.robots.mech_shoot",
            net.minecraftforge.client.settings.KeyConflictContext.IN_GAME,
            com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM,
            org.lwjgl.glfw.GLFW.GLFW_KEY_H,
            CATEGORY
    );

    public static final net.minecraft.client.KeyMapping MECH_SHOOT_ARROW = new net.minecraft.client.KeyMapping(
            "key.robots.mech_shoot_arrow",
            net.minecraftforge.client.settings.KeyConflictContext.IN_GAME,
            com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM,
            org.lwjgl.glfw.GLFW.GLFW_KEY_B,
            CATEGORY
    );
}