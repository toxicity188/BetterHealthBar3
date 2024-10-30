package kr.toxicity.healthbar.api.placeholder;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public interface PlaceholderBuilder<T> {
    @NotNull HealthBarPlaceholder<T> build(@NotNull List<String> strings);
    int requiredArgsCount();

    static <T> PlaceholderBuilder<T> of(int length, @NotNull Class<T> clazz, @NotNull Function<List<String>, Function<HealthBarCreateEvent, T>> tFunction) {
        return new PlaceholderBuilder<>() {
            @Override
            public @NotNull HealthBarPlaceholder<T> build(@NotNull List<String> strings) {
                var get = tFunction.apply(strings);
                return new HealthBarPlaceholder<>() {
                    @NotNull
                    @Override
                    public Class<T> type() {
                        return clazz;
                    }

                    @NotNull
                    @Override
                    public T value(@NotNull HealthBarCreateEvent event) {
                        return get.apply(event);
                    }
                };
            }

            @Override
            public int requiredArgsCount() {
                return length;
            }
        };
    }
}
