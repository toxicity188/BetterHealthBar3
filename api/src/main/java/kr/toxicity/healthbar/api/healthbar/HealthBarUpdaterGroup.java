package kr.toxicity.healthbar.api.healthbar;

import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import kr.toxicity.healthbar.api.player.HealthBarPlayer;
import kr.toxicity.healthbar.api.trigger.HealthBarTrigger;
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
        if (updaters.isEmpty()) return false;
        updaters.removeIf(u -> !u.update());
        return true;
    }
    default void remove() {
        var updaters = updaters();
        updaters.forEach(HealthBarUpdater::remove);
        updaters.clear();
    }

    void addHealthBar(@NotNull HealthBar healthBar, @NotNull HealthBarTrigger trigger);
}
