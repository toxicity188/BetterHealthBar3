package kr.toxicity.healthbar.api.entity;

import kr.toxicity.healthbar.api.mob.HealthBarMob;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HealthBarEntity extends Comparable<HealthBarEntity> {
    @NotNull
    LivingEntity entity();
    @Nullable
    HealthBarMob mob();
}
