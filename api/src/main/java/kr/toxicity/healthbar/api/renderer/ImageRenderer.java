package kr.toxicity.healthbar.api.renderer;

import kr.toxicity.healthbar.api.component.PixelComponent;
import org.jetbrains.annotations.NotNull;

public interface ImageRenderer extends Renderer {
    @NotNull
    PixelComponent render(int groupCount);
}
