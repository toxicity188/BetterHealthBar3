package kr.toxicity.healthbar.api.nms;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface VirtualTextDisplay {
    void teleport(@NotNull Location location);
    void remove();
    void scale(@NotNull Vector vector);
    void text(@NotNull Component component);
}
