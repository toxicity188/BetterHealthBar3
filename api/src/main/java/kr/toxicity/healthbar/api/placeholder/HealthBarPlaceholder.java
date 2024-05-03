package kr.toxicity.healthbar.api.placeholder;

import kr.toxicity.healthbar.api.healthbar.HealthBarPair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HealthBarPlaceholder<T> {
    @NotNull
    Class<T> type();
    @NotNull
    T value(@NotNull HealthBarPair player);

    default @Nullable T cast(@NotNull Object object) {
        return type().isAssignableFrom(object.getClass()) ? type().cast(object) : null;
    }
}

