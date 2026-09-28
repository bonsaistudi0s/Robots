package dev.xylonity.bonsai.robots.proxy;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;

public interface IProxy {

    default void registerClientEvents() {
        ;;
    }

    default void onMechFootstep(AbstractMechEntity mech) {
        ;;
    }

}