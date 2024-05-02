package kr.toxicity.healthbar.api.image;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public record ProcessedImage(
        @NotNull BufferedImage image,
        int xOffset,
        int yOffset
) {
}
