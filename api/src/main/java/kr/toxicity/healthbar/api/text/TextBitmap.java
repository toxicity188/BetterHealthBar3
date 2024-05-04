package kr.toxicity.healthbar.api.text;

import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public record TextBitmap(@NotNull BufferedImage image, @NotNull JsonArray array) {
}
