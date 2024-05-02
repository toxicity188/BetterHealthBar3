package kr.toxicity.healthbar.api.condition;

import kr.toxicity.healthbar.api.placeholder.HealthBarPlaceholder;
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface HealthBarOperation<T> {
    HealthBarOperationCondition<Double> NUMBER = new HealthBarOperationCondition<>(Double.class)
            .add("==", Double::equals)
            .add("!=", (a, b) -> !a.equals(b))
            .add(">=", (a, b) -> a >= b)
            .add("<=", (a, b) -> a <= b)
            .add(">", (a, b) -> a > b)
            .add("<", (a, b) -> a < b)
            ;
    HealthBarOperationCondition<Boolean> BOOL = new HealthBarOperationCondition<>(Boolean.class)
            .add("==", (a, b) -> a == b)
            .add("!=", (a, b) -> a != b)
            .add("and", (a, b) -> a && b)
            .add("or", (a, b) -> a || b)
            ;
    HealthBarOperationCondition<String> STRING = new HealthBarOperationCondition<>(String.class)
            .add("==", String::equals)
            .add("!=", (a, b) -> !a.equals(b))
            ;

    boolean invoke(@NotNull T a, @NotNull T b);

    default HealthBarOperation<T> not() {
        return (a, b) -> !invoke(a, b);
    }
    default HealthBarOperation<T> and(@NotNull HealthBarOperation<T> other) {
        return (a, b) -> invoke(a, b) && other.invoke(a, b);
    }
    default HealthBarOperation<T> or(@NotNull HealthBarOperation<T> other) {
        return (a, b) -> invoke(a, b) || other.invoke(a, b);
    }

    static <T> @Nullable HealthBarOperation<T> find(@NotNull Class<T> clazz, @NotNull String name) {
        return HealthBarOperationCondition.find(clazz, name);
    }
    static <T> @NotNull HealthBarCondition of(@NotNull HealthBarPlaceholder<T> one, @NotNull HealthBarPlaceholder<T> other, @NotNull HealthBarOperation<T> condition) {
        return t -> condition.invoke(one.value(t), other.value(t));
    }
    @SuppressWarnings("unchecked")
    static @NotNull HealthBarCondition of(@NotNull String one, @NotNull String two, @NotNull String condition) {
        var parseOne = (HealthBarPlaceholder<Object>) PlaceholderContainer.parse(one);
        var parseTwo = PlaceholderContainer.parse(two);
        if (parseOne.type() != parseTwo.type()) throw new RuntimeException("type mismatch: " + parseOne.type().getSimpleName() + " between " + parseTwo.type().getSimpleName() + ".");
        var operation = Objects.requireNonNull(find(parseOne.type(), condition), "Unable to find this operation: " + condition);
        return p -> operation.invoke(parseOne.value(p), parseTwo.value(p));
    }

    static @NotNull HealthBarCondition of(@NotNull ConfigurationSection section) {
        var first = Objects.requireNonNull(section.getString("first"), "Unable to find 'first' configuration.");
        var second = Objects.requireNonNull(section.getString("second"), "Unable to find 'second' configuration.");
        var operation = Objects.requireNonNull(section.getString("operation"), "Unable to find 'operation' configuration.");
        return of(first, second, operation);
    }
}
