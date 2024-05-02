package kr.toxicity.healthbar.api.nms;

import kr.toxicity.healthbar.api.player.HealthBarPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMS {
    @NotNull
    Player foliaAdapt(@NotNull Player player);
    @NotNull
    LivingEntity foliaAdapt(@NotNull LivingEntity entity);

    @NotNull VirtualTextDisplay createTextDisplay(@NotNull Player player, @NotNull Location location, @NotNull Component component);

    void inject(@NotNull HealthBarPlayer player);
    void uninject(@NotNull HealthBarPlayer player);
}
