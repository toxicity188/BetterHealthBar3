package kr.toxicity.healthbar.api.nms;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import kr.toxicity.healthbar.api.player.HealthBarPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PacketBundler {
    void send(@NotNull Player player);
    default void send(@NotNull HealthBarPlayer player) {
        send(player.player());
    }
    default void send(@NotNull HealthBarCreateEvent event) {
        send(event.getPlayer());
    }
}
