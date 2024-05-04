package kr.toxicity.healthbar.api.layout;

import kr.toxicity.healthbar.api.healthbar.HealthBarPair;
import kr.toxicity.healthbar.api.renderer.TextRenderer;
import kr.toxicity.healthbar.api.text.TextAlign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.function.Function;

public interface TextLayout extends Layout {
    @NotNull
    @Unmodifiable
    Map<Character, Integer> charWidth();

    @NotNull
    TextAlign align();

    @NotNull
    Function<HealthBarPair, String> pattern();

    @NotNull
    TextRenderer createRenderer(@NotNull HealthBarPair pair);
}
