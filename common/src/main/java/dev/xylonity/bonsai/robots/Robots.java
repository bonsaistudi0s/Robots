package dev.xylonity.bonsai.robots;

import dev.xylonity.bonsai.robots.network.RobotsNetwork;
import dev.xylonity.bonsai.robots.platform.RobotsPlatform;
import dev.xylonity.bonsai.robots.proxy.IProxy;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.bonsai.robots.registry.RobotsEntities;
import dev.xylonity.knightlib.api.network.Network;
import dev.xylonity.knightlib.api.network.NetworkEndpoint;
import dev.xylonity.knightlib.api.util.ResourceLocations;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class Robots {

    public static final String MOD_ID = "robots";
    public static final Logger LOGGER = LoggerFactory.getLogger("Robots");

    public static final RobotsPlatform PLATFORM = ServiceLoader.load(RobotsPlatform.class).findFirst().orElseThrow();

    public static final NetworkEndpoint NETWORK = Network.endpoint(MOD_ID);

    public static IProxy PROXY;

    public static void init() {
        RobotsEntities.ENTITIES.init();

        RobotsAbilities.init();

        RobotsNetwork.register();
    }

    public static ResourceLocation of(final String path) {
        return ResourceLocations.of(MOD_ID, path);
    }

}