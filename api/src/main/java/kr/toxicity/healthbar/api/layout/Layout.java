package kr.toxicity.healthbar.api.layout;

import kr.toxicity.healthbar.api.condition.HealthBarCondition;
import org.jetbrains.annotations.NotNull;

public interface Layout {
    int layer();
    int x();
    int y();
    int groupX();
    int groupY();
    double scale();
    @NotNull
    HealthBarCondition condition();
}
