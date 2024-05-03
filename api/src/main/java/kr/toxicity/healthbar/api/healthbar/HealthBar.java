package kr.toxicity.healthbar.api.healthbar;

import kr.toxicity.healthbar.api.configuration.HealthBarConfiguration;
import kr.toxicity.healthbar.api.layout.LayoutGroup;
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface HealthBar extends HealthBarConfiguration {
    @NotNull
    UUID uuid();
    @NotNull
    @Unmodifiable
    List<LayoutGroup> groups();

    @NotNull
    @Unmodifiable
    Set<HealthBarTrigger> triggers();

    int duration();

    @NotNull
    HealthBarRenderer createRenderer(@NotNull HealthBarPair pair);
}
