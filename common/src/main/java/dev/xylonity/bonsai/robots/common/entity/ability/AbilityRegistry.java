package dev.xylonity.bonsai.robots.common.entity.ability;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AbilityRegistry {

    private static final Map<ResourceLocation, MechAbility> ABILITIES = new ConcurrentHashMap<>();

    private AbilityRegistry() {
        ;;
    }

    public static <T extends MechAbility> T register(ResourceLocation id, T ability) {
        if (ABILITIES.putIfAbsent(id, ability) != null) {
            throw new IllegalArgumentException("[Robots] Duplicate mech ability id: " + id);
        }

        return ability;
    }

    @Nullable
    public static MechAbility get(ResourceLocation id) {
        return ABILITIES.get(id);
    }

}