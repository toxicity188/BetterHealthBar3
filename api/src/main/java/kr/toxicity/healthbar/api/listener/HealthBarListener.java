package kr.toxicity.healthbar.api.listener;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import org.jetbrains.annotations.NotNull;

public interface HealthBarListener {
    HealthBarListener ZERO = e -> 0;
    HealthBarListener INVALID = e -> -1;

    double value(@NotNull HealthBarCreateEvent entity);
}
