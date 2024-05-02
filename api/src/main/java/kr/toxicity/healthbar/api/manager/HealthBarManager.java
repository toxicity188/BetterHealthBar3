package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.healthbar.HealthBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public interface HealthBarManager {
    @Nullable
    HealthBar healthBar(@NotNull String name);

    @NotNull
    @Unmodifiable
    Collection<HealthBar> allHealthBars();
}
