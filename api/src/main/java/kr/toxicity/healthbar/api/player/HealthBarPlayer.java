package kr.toxicity.healthbar.api.player;

import kr.toxicity.healthbar.api.healthbar.HealthBar;
import kr.toxicity.healthbar.api.healthbar.HealthBarUpdaterGroup;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface HealthBarPlayer extends Comparable<HealthBarPlayer> {
    @NotNull
    Player player();
    void uninject();
    Map<UUID, HealthBarUpdaterGroup> updaterMap();
    void showHealthBar(@NotNull HealthBar healthBar, @NotNull LivingEntity entity);
}
