package dev.xylonity.bonsai.robots.common.event;

import dev.xylonity.bonsai.robots.common.entity.mech.TallMechEntity;
import dev.xylonity.bonsai.robots.common.entity.mech.TankMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsEntities;
import dev.xylonity.knightlib.api.event.RegisterEvent;
import dev.xylonity.knightlib.api.event.impl.server.EntityAttributeRegistrationEvent;

public class RobotsCommonEvents {

    @RegisterEvent
    public static void onAttributeRegistration(EntityAttributeRegistrationEvent event) {
        event.register(RobotsEntities.TANK_MECH, TankMechEntity::createAttributes);
        event.register(RobotsEntities.TALL_MECH, TallMechEntity::createAttributes);
    }

}
