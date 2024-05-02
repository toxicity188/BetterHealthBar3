package kr.toxicity.healthbar.api.healthbar;

import kr.toxicity.healthbar.api.nms.VirtualTextDisplay;
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer;
import org.jetbrains.annotations.NotNull;

public interface HealthBarUpdater {
    @NotNull
    HealthBarUpdaterGroup parent();
    @NotNull
    VirtualTextDisplay display();
    @NotNull
    HealthBarRenderer renderer();

    void updateTick();

    boolean update();
}
