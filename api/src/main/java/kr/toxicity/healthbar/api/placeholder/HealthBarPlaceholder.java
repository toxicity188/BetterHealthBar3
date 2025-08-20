package kr.toxicity.healthbar.api.placeholder;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface HealthBarPlaceholder<T> {
    @NotNull
    Class<T> type();
    @Nullable
    T value(@NotNull HealthBarCreateEvent event);

    default @NotNull HealthBarPlaceholder<T> assertNumber(@NotNull Supplier<String> messageSupplier) {
        return assertClass(Number.class, messageSupplier);
    }

    default @NotNull HealthBarPlaceholder<T> assertClass(@NotNull Class<?> type, @NotNull Supplier<String> messageSupplier) {
        if (!type.isAssignableFrom(type())) throw new RuntimeException(messageSupplier.get());
        return this;
    }

    default @Nullable String stringValue(@NotNull HealthBarCreateEvent event) {
        var value = value(event);
        return value != null ? value.toString() : "<error>";
    }
}

