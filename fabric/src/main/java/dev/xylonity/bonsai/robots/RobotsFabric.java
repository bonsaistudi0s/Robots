package dev.xylonity.bonsai.robots;

import dev.xylonity.bonsai.robots.common.CommonProxy;
import dev.xylonity.bonsai.robots.common.event.RobotsCommonEvents;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.knightlib.api.config.ConfigComposer;
import dev.xylonity.knightlib.api.event.KnightLibEvents;
import net.fabricmc.api.ModInitializer;

public class RobotsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Robots.PROXY = new CommonProxy();

        ConfigComposer.registerConfig(Robots.MOD_ID, RobotsConfig.class);

        KnightLibEvents.SERVER.register(RobotsCommonEvents.class);

        Robots.init();
    }

}
