package kr.toxicity.healthbar.api.renderer;

import kr.toxicity.healthbar.api.component.WidthComponent;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface HealthBarRenderer extends Renderer {
    @NotNull
    RenderResult render();

    void updateTick();

    record RenderResult(@NotNull WidthComponent component, @NotNull Location location) {
    }
}
