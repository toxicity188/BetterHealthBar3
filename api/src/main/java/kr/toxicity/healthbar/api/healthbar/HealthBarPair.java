package kr.toxicity.healthbar.api.healthbar;

import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import kr.toxicity.healthbar.api.player.HealthBarPlayer;
import org.jetbrains.annotations.NotNull;

public record HealthBarPair(@NotNull HealthBarPlayer player, @NotNull HealthBarEntity entity) {
}
