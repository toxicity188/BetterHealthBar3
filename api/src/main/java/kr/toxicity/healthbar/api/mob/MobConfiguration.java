package kr.toxicity.healthbar.api.mob;

import kr.toxicity.healthbar.api.configuration.HealthBarConfiguration;
import kr.toxicity.healthbar.api.healthbar.HealthBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface MobConfiguration extends HealthBarConfiguration {
    @NotNull
    Set<String> types();
    double height();
    boolean blacklist();
    @NotNull
    @Unmodifiable
    Set<HealthBar> healthBars();
}
