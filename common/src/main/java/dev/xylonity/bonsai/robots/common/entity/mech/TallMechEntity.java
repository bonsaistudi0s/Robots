package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.RawAnimation;

public class TallMechEntity extends AbstractMechEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.tall_mech.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.tall_mech.walk");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.tall_mech.death");
    private static final RawAnimation ACTIVATE_IDLE = RawAnimation.begin().thenLoop("animation.tall_mech.activate_idle");
    private static final RawAnimation ACTIVATE = RawAnimation.begin().thenPlay("animation.tall_mech.activate");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.tall_mech.run_ability");
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlay("animation.tall_mech.jump");

    public TallMechEntity(EntityType<? extends TallMechEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMechAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.17225D);
    }

    @Override
    protected RawAnimation getIdleAnim() {
        return IDLE;
    }

    @Override
    protected RawAnimation getWalkAnim() {
        return WALK;
    }

    @Override
    protected RawAnimation getDeathAnim() {
        return DEATH;
    }

    @Override
    protected RawAnimation getActivateIdleAnim() {
        return ACTIVATE_IDLE;
    }

    @Override
    protected RawAnimation getActivateAnim() {
        return ACTIVATE;
    }

    // animation.tall_mech.activate is 1.2917s long (~25.8 ticks) + a 1s hold before it's mountable
    @Override
    protected int activationDurationTicks() {
        return 26 + 20;
    }

    @Override
    protected RawAnimation getRunAnim() {
        return RUN;
    }

    @Override
    protected float getSprintSpeedMultiplier() {
        return 3.0F;
    }

    // animation.tall_mech.run_ability is a 0.5s cycle (getRunBlocksPerCycle derives itself from this and the sprint multiplier)
    @Override
    protected float getRunCycleSeconds() {
        return 0.5F;
    }

    @Override
    protected RawAnimation getJumpAnim() {
        return JUMP;
    }

    // Big mech, big jump: ~7 blocks at full charge
    @Override
    protected float getJumpStrength() {
        return 1.1F;
    }

}
