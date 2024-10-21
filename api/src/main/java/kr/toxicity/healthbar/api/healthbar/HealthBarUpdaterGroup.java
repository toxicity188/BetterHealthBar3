package kr.toxicity.healthbar.api.healthbar;

import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import kr.toxicity.healthbar.api.player.HealthBarPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface HealthBarUpdaterGroup {
    @NotNull
    HealthBarEntity entity();
    @NotNull
    Collection<HealthBarUpdater> updaters();
    @NotNull
    HealthBarPlayer player();

    default boolean update() {
        var updaters = updaters();
        updaters.removeIf(u -> !u.update());
        return !updaters.isEmpty();
    }
    default void remove() {
        var updaters = updaters();
        updaters.forEach(HealthBarUpdater::remove);
        updaters.clear();
    }

    void addHealthBar(@NotNull HealthBarCreateEvent data);
}
