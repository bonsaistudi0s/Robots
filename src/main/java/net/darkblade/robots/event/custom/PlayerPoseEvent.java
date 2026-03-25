package net.darkblade.robots.event.custom;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class PlayerPoseEvent<T extends LivingEntity> extends Event {
    private final T entity;
    private final HumanoidModel<T> model;

    public PlayerPoseEvent(T entity, HumanoidModel<T> model) {
        this.entity = entity;
        this.model = model;
    }

    public T getEntity() { return entity; }
    public HumanoidModel<T> getModel() { return model; }
}