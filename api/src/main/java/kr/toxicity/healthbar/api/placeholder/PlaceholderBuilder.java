package kr.toxicity.healthbar.api.placeholder;

import kr.toxicity.healthbar.api.healthbar.HealthBarPair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public interface PlaceholderBuilder<T> {
    HealthBarPlaceholder<T> build(@NotNull List<String> strings);
    int requiredArgsCount();

    static <T> PlaceholderBuilder<T> of(int length, @NotNull Class<T> clazz, @NotNull Function<List<String>, Function<HealthBarPair, T>> tFunction) {
        return new PlaceholderBuilder<>() {
            @Override
            public HealthBarPlaceholder<T> build(@NotNull List<String> strings) {
                var get = tFunction.apply(strings);
                return new HealthBarPlaceholder<T>() {
                    @NotNull
                    @Override
                    public Class<T> type() {
                        return clazz;
                    }

                    @NotNull
                    @Override
                    public T value(@NotNull HealthBarPair player) {
                        return get.apply(player);
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
