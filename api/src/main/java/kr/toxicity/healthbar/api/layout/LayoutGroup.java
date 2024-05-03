package kr.toxicity.healthbar.api.layout;

import kr.toxicity.healthbar.api.configuration.HealthBarConfiguration;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface LayoutGroup extends HealthBarConfiguration {
    @NotNull
    @Unmodifiable
    List<ImageLayout> images();

    @Nullable
    String group();

    @NotNull
    Key imageKey();
}
