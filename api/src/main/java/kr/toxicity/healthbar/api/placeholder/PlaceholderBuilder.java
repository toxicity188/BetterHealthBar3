package kr.toxicity.healthbar.api.placeholder;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public interface PlaceholderBuilder<T> {
    @NotNull HealthBarPlaceholder<T> build(@NotNull PlaceholderOption.Property property, @NotNull List<String> strings);
    int requiredArgsCount();

    static <R> @NotNull Builder<R> builder(@NotNull PlaceholderContainer<R> container) {
        return new Builder<>(Objects.requireNonNull(container));
    }

    static <R> @Nullable Builder<R> builder(@NotNull Class<R> clazz) {
        var get = PlaceholderContainer.find(clazz);
        return get != null ? get.create() : null;
    }

    class Builder<R> {
        private final PlaceholderContainer<R> container;
        private Function<R, String> stringMapper;
        private Function<List<String>, Function<HealthBarCreateEvent, R>> parser;
        private int argsLength;
        private Builder(@NotNull PlaceholderContainer<R> container) {
            this.container = container;
        }

        public @NotNull Builder<R> stringMapper(@Nullable Function<R, String> mapper) {
            stringMapper = mapper;
            return this;
        }
        public @NotNull Builder<R> parser(@NotNull Function<List<String>, Function<HealthBarCreateEvent, R>> parser) {
            this.parser = Objects.requireNonNull(parser);
            return this;
        }
        public @NotNull Builder<R> argsLength(int argsLength) {
            this.argsLength = argsLength;
            return this;
        }

        public @NotNull PlaceholderBuilder<R> build() {
            return new PlaceholderBuilder<>() {
                @Override
                public @NotNull HealthBarPlaceholder<R> build(@NotNull PlaceholderOption.Property property, @NotNull List<String> strings) {
                    var valueFunction = parser.apply(strings);
                    var result = container.propertyResult(property);
                    var mapper = stringMapper != null ? stringMapper : result.stringMapper();
                    return new HealthBarPlaceholder<>() {
                        @Override
                        public @NotNull Class<R> type() {
                            return container.clazz();
                        }

                        @Override
                        public @Nullable R value(@NotNull HealthBarCreateEvent event) {
                            return valueFunction.apply(event);
                        }

                        @Override
                        public @Nullable String stringValue(@NotNull HealthBarCreateEvent event) {
                            var parsed = value(event);
                            return parsed != null ? mapper.apply(parsed) : null;
                        }
                    };
                }

                @Override
                public int requiredArgsCount() {
                    return argsLength;
                }
            };
        }
    }

}
