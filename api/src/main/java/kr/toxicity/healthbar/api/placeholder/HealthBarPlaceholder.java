package kr.toxicity.healthbar.api.placeholder;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HealthBarPlaceholder<T> {
    @NotNull
    Class<T> type();
    @Nullable
    T value(@NotNull HealthBarCreateEvent event);

    default @Nullable String stringValue(@NotNull HealthBarCreateEvent event) {
        var value = value(event);
        return value != null ? value.toString() : "<error>";
    }

    default @Nullable T cast(@NotNull Object object) {
        return type().isAssignableFrom(object.getClass()) ? type().cast(object) : null;
    }
}

