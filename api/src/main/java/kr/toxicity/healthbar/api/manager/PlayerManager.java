package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.player.HealthBarPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerManager {
    @NotNull
    HealthBarPlayer player(@NotNull Player player);
    @Nullable
    HealthBarPlayer player(@NotNull UUID uuid);
}
