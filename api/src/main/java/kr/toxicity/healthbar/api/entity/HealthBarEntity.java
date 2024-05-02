package kr.toxicity.healthbar.api.entity;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface HealthBarEntity extends Comparable<HealthBarEntity> {
    @NotNull
    LivingEntity entity();
}
