package dev.xylonity.bonsai.robots.common.entity.mech;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsAbilities;
import dev.xylonity.knightlib.api.animation.KnightLibAnim;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TankMechEntity extends AbstractMechEntity {

    private static final KnightLibAnim IDLE = KnightLibAnim.begin().thenLoop("animation.tank_mech.idle");
    private static final KnightLibAnim WALK = KnightLibAnim.begin().thenLoop("animation.tank_mech.walk");
    private static final KnightLibAnim ATTACK = KnightLibAnim.begin().thenPlay("animation.tank_mech.attack");
    private static final KnightLibAnim AIM = KnightLibAnim.begin().thenPlayAndHold("animation.tank_mech.aim");
    private static final KnightLibAnim AIM_OFF = KnightLibAnim.begin().thenPlay("animation.tank_mech.aim_off");
    private static final KnightLibAnim SHOOT_HAND = KnightLibAnim.begin().thenPlay("animation.tank_mech.shoot_hand");

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
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            final double y = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
            callback.accept(passenger, this.getX(), y + 1, this.getZ());
        }

    }

    @Override
    protected KnightLibAnim getIdleAnim() {
        return IDLE;
    }

    @Override
    protected KnightLibAnim getWalkAnim() {
        return WALK;
    }

    //@Override
    //public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    //    super.registerControllers(controllers);

    //    controllers.add(new AnimationController<>(this, "attack", 2, state -> PlayState.STOP)
    //            .triggerableAnim("attack", ATTACK));

    //    controllers.add(new AnimationController<>(this, "aim", state -> PlayState.STOP)
    //            .triggerableAnim("aim", AIM)
    //            .triggerableAnim("aim_off", AIM_OFF));

    //    controllers.add(new AnimationController<>(this, "shoot_hand", state -> PlayState.STOP)
    //            .triggerableAnim("shoot_hand", SHOOT_HAND));
    //}

    //public void playAttackAnimation() {
    //    triggerAnim("attack", "attack");
    //}

    //public void playAimAnimation() {
    //    triggerAnim("aim", "aim");
    // }

    //public void playAimOffAnimation() {
    //    triggerAnim("aim", "aim_off");
    //}

    //public void playShootHandAnimation() {
    //    triggerAnim("shoot_hand", "shoot_hand");
    //}

}
