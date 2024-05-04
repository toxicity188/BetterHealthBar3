package kr.toxicity.healthbar.api.trigger;

import org.jetbrains.annotations.NotNull;

public interface HealthBarTrigger {
    @NotNull HealthBarTriggerType type();
}
