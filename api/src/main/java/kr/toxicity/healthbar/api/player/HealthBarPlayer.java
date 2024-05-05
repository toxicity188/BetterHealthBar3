package kr.toxicity.healthbar.api.player;

import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import kr.toxicity.healthbar.api.healthbar.HealthBar;
import kr.toxicity.healthbar.api.trigger.HealthBarTrigger;
import kr.toxicity.healthbar.api.healthbar.HealthBarUpdaterGroup;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface HealthBarPlayer extends Comparable<HealthBarPlayer> {
    @NotNull
    Player player();
    void uninject();
    void clear();
    @NotNull
    Map<UUID, HealthBarUpdaterGroup> updaterMap();
    void showHealthBar(@NotNull HealthBar healthBar, @NotNull HealthBarTrigger trigger, @NotNull HealthBarEntity entity);
}
