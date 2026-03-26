package net.darkblade.robots.event;

import com.mojang.math.Axis;
import net.darkblade.robots.Robots;
import net.darkblade.robots.client.RobotsKeybindings;
import net.darkblade.robots.client.renderer.TankMechRenderer;
import net.darkblade.robots.client.renderer.layer.MechRiderLayer;
import net.darkblade.robots.entity.TankMechEntity;
import net.darkblade.robots.event.custom.ModelRotationEvent;
import net.darkblade.robots.event.custom.PlayerPoseEvent;
import net.darkblade.robots.network.MechAttackC2SPacket;
import net.darkblade.robots.network.MechShootArrowC2SPacket;
import net.darkblade.robots.network.MechShootC2SPacket;
import net.darkblade.robots.network.RobotsPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Robots.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (MechRiderLayer.BLOCKED_RENDERS.remove(event.getEntity().getUUID())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onModelRotation(ModelRotationEvent<?> event) {
        if (event.getEntity() instanceof Player player
                && player.getVehicle() instanceof TankMechEntity) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerPose(PlayerPoseEvent<?> event) {
        if (event.getEntity().getVehicle() instanceof TankMechEntity) {
            var m = event.getModel();

            m.rightArm.xRot = (float) Math.toRadians(-25.0F);
            m.rightArm.yRot = 0;
            m.rightArm.zRot = (float) Math.toRadians(15.0F);
            m.leftArm.xRot  = (float) Math.toRadians(-25.0F);
            m.leftArm.yRot  = 0;
            m.leftArm.zRot  = (float) Math.toRadians(-15.0F);


            m.rightLeg.xRot = (float) Math.toRadians(-65.0F);
            m.rightLeg.yRot = (float) Math.toRadians(15.0F);
            m.rightLeg.zRot = (float) Math.toRadians(15.0F);
            m.leftLeg.xRot  = (float) Math.toRadians(-65.0F);
            m.leftLeg.yRot  = (float) Math.toRadians(-15.0F);
            m.leftLeg.zRot  = (float) Math.toRadians(-15.0F);

            m.head.xRot = 0;
            m.head.yRot = 0;
            m.hat.xRot  = 0;
            m.hat.yRot  = 0;

            m.body.xRot = 0;
            m.body.yRot = 0;
            m.body.zRot = 0;

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof TankMechEntity mech) {

            while (RobotsKeybindings.MECH_ATTACK.consumeClick()) {
                RobotsPackets.INSTANCE.sendToServer(new MechAttackC2SPacket());
            }

            while (RobotsKeybindings.MECH_SHOOT.consumeClick()) {
                EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(mech);

                if (renderer instanceof TankMechRenderer tankRenderer) {
                    Vec3 gunPos = tankRenderer.getLastHandgunBoneWorldPos();

                    if (gunPos == null) gunPos = mech.position().add(1.2, 2.0, 1.5);

                    RobotsPackets.INSTANCE.sendToServer(new MechShootC2SPacket(gunPos.x, gunPos.y, gunPos.z));
                }
            }

            while (RobotsKeybindings.MECH_SHOOT_ARROW.consumeClick()) {
                EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(mech);

                if (renderer instanceof TankMechRenderer tankRenderer) {
                    Vec3 laserPos = tankRenderer.getLastLaserBoneWorldPos();

                    if (laserPos == null) {
                        laserPos = mech.position().add(0, 2.5, 0);
                    }

                    RobotsPackets.INSTANCE.sendToServer(new MechShootArrowC2SPacket(laserPos.x, laserPos.y, laserPos.z));
                }
            }
        }
    }
}