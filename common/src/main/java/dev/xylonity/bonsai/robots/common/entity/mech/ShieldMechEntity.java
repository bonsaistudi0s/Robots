package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.config.RobotsConfig;
import dev.xylonity.knightlib.api.animation.KnightLibAnim;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class ShieldMechEntity extends AbstractMechEntity {

    private static final KnightLibAnim IDLE = KnightLibAnim.begin().thenLoop("animation.shield_mech.idle");
    private static final KnightLibAnim WALK = KnightLibAnim.begin().thenLoop("animation.shield_mech.walk");
    private static final KnightLibAnim DEACTIVATED = KnightLibAnim.begin().thenLoop("animation.shield_mech.deactivated");
    private static final KnightLibAnim ACTIVATE = KnightLibAnim.begin().thenPlayAndHold("animation.shield_mech.activate");

    public ShieldMechEntity(EntityType<? extends AbstractMechEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMechAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.16225D)
                .add(Attributes.ATTACK_DAMAGE, RobotsConfig.TANK_MECH_MELEE_DAMAGE);
    }

    @Override
    protected KnightLibAnim getIdleAnimation() {
        return IDLE;
    }

    @Override
    protected KnightLibAnim getMovementAnimation() {
        return WALK;
    }

    @Override
    protected int getActivationDurationTicks() {
        return 51;
    }

}
