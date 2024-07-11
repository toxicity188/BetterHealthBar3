package kr.toxicity.healthbar.api.renderer;

import kr.toxicity.healthbar.api.nms.VirtualTextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface HealthBarRenderer extends Renderer {
    @NotNull
    @Unmodifiable
    List<VirtualTextDisplay> displays();
    void updateTick();
    boolean work();
    void stop();
}
