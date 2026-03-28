package net.darkblade.robots;

import com.mojang.logging.LogUtils;
import net.darkblade.robots.client.RobotsKeybindings;
import net.darkblade.robots.client.renderer.TankMechRenderer;
import net.darkblade.robots.client.renderer.projectile.BigLaserRenderer;
import net.darkblade.robots.client.renderer.projectile.LaserRenderer;
import net.darkblade.robots.client.renderer.projectile.RocketRenderer;
import net.darkblade.robots.entity.RobotsEntitys;
import net.darkblade.robots.entity.TankMechEntity;
import net.darkblade.robots.item.RobotsItems;
import net.darkblade.robots.network.RobotsPackets;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Robots.MODID)
public class Robots {

    public static final String MODID = "robots";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Robots(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        RobotsEntitys.register(modEventBus);
        RobotsItems.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerEntityAttributes);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(RobotsEntitys.TANK_MECH.get(), TankMechEntity.createAttributes().build());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RobotsPackets.register();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(RobotsItems.TANK_MECH_SPAWN_EGG);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) { }


    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) { }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(RobotsEntitys.TANK_MECH.get(), TankMechRenderer::new);
            event.registerEntityRenderer(RobotsEntitys.LASER.get(), LaserRenderer::new);
            event.registerEntityRenderer(RobotsEntitys.BIG_LASER.get(), BigLaserRenderer::new);
            event.registerEntityRenderer(RobotsEntitys.ROCKET.get(), RocketRenderer::new);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(RobotsKeybindings.MECH_ATTACK);
        }
    }
}