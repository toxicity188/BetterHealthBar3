package kr.toxicity.healthbar.api.mob;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MobProvider {
    @Nullable
    HealthBarMob provide(@NotNull LivingEntity entity);
}
