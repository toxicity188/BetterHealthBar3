package kr.toxicity.healthbar.api.trigger;

import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public record BukkitTrigger(@NotNull HealthBarTriggerType type, @NotNull EntityEvent event) implements HealthBarTrigger {
}
