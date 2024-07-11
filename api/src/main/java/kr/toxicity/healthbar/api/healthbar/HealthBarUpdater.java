package kr.toxicity.healthbar.api.healthbar;

import kr.toxicity.healthbar.api.renderer.HealthBarRenderer;
import org.jetbrains.annotations.NotNull;

public interface HealthBarUpdater {
    @NotNull
    HealthBarUpdaterGroup parent();
    @NotNull
    HealthBarRenderer renderer();

    void updateTick();
    void remove();

    boolean update();
}
