package kr.toxicity.healthbar.api.layout;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import kr.toxicity.healthbar.api.renderer.TextRenderer;
import kr.toxicity.healthbar.api.text.TextAlign;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.function.Function;

public interface TextLayout extends Layout {
    @NotNull
    @Unmodifiable
    Map<Integer, Integer> charWidth();

    @NotNull
    TextAlign align();

    @NotNull
    Function<HealthBarCreateEvent, Component> pattern();

    @NotNull
    TextRenderer createRenderer(@NotNull HealthBarCreateEvent pair);
}
