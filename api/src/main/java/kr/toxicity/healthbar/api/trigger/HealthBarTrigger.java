package kr.toxicity.healthbar.api.trigger;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface HealthBarTrigger {
    @NotNull HealthBarTriggerType type();
}
