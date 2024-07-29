package kr.toxicity.healthbar.api.healthbar;

import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import kr.toxicity.healthbar.api.player.HealthBarPlayer;
import kr.toxicity.healthbar.api.trigger.HealthBarTrigger;
import org.jetbrains.annotations.NotNull;

public record HealthBarData(
        @NotNull HealthBar healthBar,
        @NotNull HealthBarTrigger trigger,
        @NotNull HealthBarPlayer player,
        @NotNull HealthBarEntity entity
) {
}
