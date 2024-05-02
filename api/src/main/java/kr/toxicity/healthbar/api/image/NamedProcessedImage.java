package kr.toxicity.healthbar.api.image;

import org.jetbrains.annotations.NotNull;

public record NamedProcessedImage(@NotNull String name, @NotNull ProcessedImage image) {
}
