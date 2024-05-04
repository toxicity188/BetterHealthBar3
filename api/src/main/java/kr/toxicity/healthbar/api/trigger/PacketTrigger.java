package kr.toxicity.healthbar.api.trigger;

import org.jetbrains.annotations.NotNull;

public record PacketTrigger(@NotNull HealthBarTriggerType type, @NotNull Object handle) implements HealthBarTrigger {
}
