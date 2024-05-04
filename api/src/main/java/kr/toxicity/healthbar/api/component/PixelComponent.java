package kr.toxicity.healthbar.api.component;

import org.jetbrains.annotations.NotNull;

public record PixelComponent(int pixel, @NotNull WidthComponent component) {
    public boolean isEmpty() {
        return pixel == 0;
    }
}
