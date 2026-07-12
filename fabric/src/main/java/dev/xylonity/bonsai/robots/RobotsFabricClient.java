package dev.xylonity.bonsai.robots;

import dev.xylonity.bonsai.robots.client.ClientProxy;
import net.fabricmc.api.ClientModInitializer;

public class RobotsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Robots.PROXY = new ClientProxy();

        //Robots.PROXY.registerClientEvents();
    }

}
