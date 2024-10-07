package kr.toxicity.healthbar.api.text;

import kr.toxicity.healthbar.api.configuration.HealthBarConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public interface HealthBarText extends HealthBarConfiguration {
    @NotNull
    @Unmodifiable
    Map<Integer, Integer> chatWidth();

    @NotNull
    @Unmodifiable
    List<TextBitmap> bitmap();

    int height();
}
