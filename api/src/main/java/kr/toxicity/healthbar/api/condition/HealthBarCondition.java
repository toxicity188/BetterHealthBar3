package kr.toxicity.healthbar.api.condition;

import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface HealthBarCondition extends Function<HealthBarEntity, Boolean> {
    HealthBarCondition TRUE = p -> true;

    default @NotNull HealthBarCondition not() {
        return p -> !apply(p);
    }
}
