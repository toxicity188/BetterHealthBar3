package kr.toxicity.healthbar.api.condition;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class HealthBarOperationCondition<T> {

    private static final Map<Class<?>, HealthBarOperationCondition<?>> CONTAINER_MAP = new HashMap<>();

    @Getter
    private final Class<T> clazz;
    private final Map<String, HealthBarOperation<T>> conditionMap = new HashMap<>();

    public HealthBarOperationCondition(Class<T> clazz) {
        CONTAINER_MAP.put(clazz, this);
        this.clazz = clazz;
    }

    public HealthBarOperationCondition<T> add(@NotNull String name, HealthBarOperation<T> condition) {
        conditionMap.put(name, condition);
        return this;
    }
    public @Nullable HealthBarOperation<T> condition(@NotNull String name) {
        return conditionMap.get(name);
    }


    @SuppressWarnings("unchecked")
    public static <T> @Nullable HealthBarOperation<T> find(@NotNull Class<T> clazz, @NotNull String name) {
        var condition = ((HealthBarOperationCondition<T>) CONTAINER_MAP.get(clazz));
        return condition != null ? condition.condition(name) : null;
    }
}
