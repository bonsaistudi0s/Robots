package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.List;

public class TankMechEntity extends AbstractMechEntity {

    public TankMechEntity(EntityType<? extends TankMechEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMechAttributes().add(Attributes.MAX_HEALTH, 120.0D);
    }

    @Override
    public List<ResourceLocation> getAbilities() {
        return List.of();
    }

}
