package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.text.HealthBarText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TextManager {
    @Nullable
    HealthBarText text(@NotNull String name);
}
