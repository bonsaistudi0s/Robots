package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class TankMechEntity extends AbstractMechEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.tank_mech.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.tank_mech.walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.tank_mech.attack");
    private static final RawAnimation AIM = RawAnimation.begin().thenPlayAndHold("animation.tank_mech.aim");
    private static final RawAnimation AIM_OFF = RawAnimation.begin().thenPlay("animation.tank_mech.aim_off");
    private static final RawAnimation SHOOT_HAND = RawAnimation.begin().thenPlay("animation.tank_mech.shoot_hand");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.tank_mech.death");
    private static final RawAnimation ACTIVATE_IDLE = RawAnimation.begin().thenLoop("animation.tank_mech.activate_idle");
    private static final RawAnimation ACTIVATE = RawAnimation.begin().thenPlay("animation.tank_mech.activate");

    public TankMechEntity(EntityType<? extends TankMechEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMechAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.17225D);
    }

    @Override
    @Nullable
    public ResourceLocation getPrimaryAbility() {
        return RobotsAbilities.HIT;
    }

    @Override
    @Nullable
    public ResourceLocation getSecondaryAbility() {
        return RobotsAbilities.LAZER_SMALL;
    }

    @Override
    public List<ResourceLocation> getSpecialAbilities() {
        return List.of(RobotsAbilities.LAZER_BIG, RobotsAbilities.ROCKET, RobotsAbilities.ELECTRIC_FIELD);
    }

    @Override
    public ResourceLocation getAbilityIcon(ResourceLocation abilityId) {
        return new ResourceLocation(abilityId.getNamespace(), "textures/entity/tank_mech/icons/" + abilityId.getPath() + "_icon.png");
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

    // animation.tank_mech.activate is 2.875s long (57.5 ticks) + a 1s hold before it's mountable
    @Override
    protected int activationDurationTicks() {
        return 58 + 20;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);

        controllers.add(new AnimationController<>(this, "attack", 2, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK));

        controllers.add(new AnimationController<>(this, "aim", state -> PlayState.STOP)
                .triggerableAnim("aim", AIM)
                .triggerableAnim("aim_off", AIM_OFF));

        controllers.add(new AnimationController<>(this, "shoot_hand", state -> PlayState.STOP)
                .triggerableAnim("shoot_hand", SHOOT_HAND));
    }

    public void playAttackAnimation() {
        triggerAnim("attack", "attack");
    }

    public void playAimAnimation() {
        triggerAnim("aim", "aim");
    }

    public void playAimOffAnimation() {
        triggerAnim("aim", "aim_off");
    }

    public void playShootHandAnimation() {
        triggerAnim("shoot_hand", "shoot_hand");
    }

}
