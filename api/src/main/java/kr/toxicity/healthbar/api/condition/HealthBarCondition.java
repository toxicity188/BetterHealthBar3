package kr.toxicity.healthbar.api.condition;

import kr.toxicity.healthbar.api.healthbar.HealthBarPair;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface HealthBarCondition extends Function<HealthBarPair, Boolean> {
    HealthBarCondition TRUE = p -> true;

    default @NotNull HealthBarCondition not() {
        return p -> !apply(p);
    }
}
