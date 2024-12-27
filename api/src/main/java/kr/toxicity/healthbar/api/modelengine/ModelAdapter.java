package kr.toxicity.healthbar.api.modelengine;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ModelAdapter {
    ModelAdapter NONE = e -> null;
    @Nullable
    Double height(@NotNull Entity entity);
}
