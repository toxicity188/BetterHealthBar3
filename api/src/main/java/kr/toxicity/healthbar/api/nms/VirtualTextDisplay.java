package kr.toxicity.healthbar.api.nms;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface VirtualTextDisplay {
    void spawn(@NotNull PacketBundler bundler);
    void shadowRadius(float radius);
    void shadowStrength(float strength);
    void update(@NotNull PacketBundler bundler);
    void teleport(@NotNull Location location);
    void remove(@NotNull PacketBundler bundler);
    void transformation(@NotNull Vector location, @NotNull Vector vector);
    void text(@NotNull Component component);
}
