package net.darkblade.robots.event.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ModelRotationEvent<T extends LivingEntity> extends Event {
    private final T entity;
    private final EntityModel<T> model;
    private final PoseStack poseStack;

    public ModelRotationEvent(T entity, EntityModel<T> model, PoseStack poseStack) {
        this.entity = entity;
        this.model = model;
        this.poseStack = poseStack;
    }

    public T getEntity() { return entity; }
    public EntityModel<T> getModel() { return model; }
    public PoseStack getPoseStack() { return poseStack; }
}