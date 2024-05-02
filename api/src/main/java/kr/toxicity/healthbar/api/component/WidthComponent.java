package kr.toxicity.healthbar.api.component;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public record WidthComponent(int width, @NotNull TextComponent.Builder component) {
    public @NotNull WidthComponent plus(@NotNull WidthComponent other) {
        return new WidthComponent(width + other.width, component.append(other.component));
    }
}
