package dev.xylonity.bonsai.robots.client;

import dev.xylonity.bonsai.robots.client.event.RobotsClientEvents;
import dev.xylonity.bonsai.robots.proxy.IProxy;
import dev.xylonity.knightlib.api.event.KnightLibEvents;

public class ClientProxy implements IProxy {

    @Override
    public void registerClientEvents() {
        KnightLibEvents.CLIENT.register(RobotsClientEvents.class);
    }

}
