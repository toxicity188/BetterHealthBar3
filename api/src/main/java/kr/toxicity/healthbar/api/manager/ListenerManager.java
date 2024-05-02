package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.listener.HealthBarListener;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ListenerManager {
    void addListener(@NotNull String name, @NotNull Function<ConfigurationSection, HealthBarListener> listenerFunction);

    @NotNull
    HealthBarListener build(@NotNull ConfigurationSection section);
}
