package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import kr.toxicity.healthbar.api.mob.MobConfiguration;
import kr.toxicity.healthbar.api.mob.MobProvider;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MobManager extends MobProvider {
    @Nullable
    MobConfiguration configuration(@NotNull String name);
    @NotNull
    HealthBarEntity entity(@NotNull LivingEntity livingEntity);
    void addProvider(@NotNull MobProvider provider);
}
