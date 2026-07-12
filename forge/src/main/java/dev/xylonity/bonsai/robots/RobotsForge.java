package dev.xylonity.bonsai.robots;

import dev.xylonity.bonsai.robots.client.ClientProxy;
import dev.xylonity.bonsai.robots.common.CommonProxy;
import dev.xylonity.bonsai.robots.common.event.RobotsCommonEvents;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.knightlib.api.config.ConfigComposer;
import dev.xylonity.knightlib.api.event.KnightLibEvents;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Robots.MOD_ID)
public class RobotsForge {
    
    public RobotsForge() {
        Robots.PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ConfigComposer.registerConfig(Robots.MOD_ID, RobotsConfig.class);

        KnightLibEvents.SERVER.register(RobotsCommonEvents.class);
        Robots.PROXY.registerClientEvents();

        Robots.init();
    }

}