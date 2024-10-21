package kr.toxicity.healthbar.api.modelengine;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ModelEngineAdapter {
    ModelEngineAdapter NONE = e -> null;
    @Nullable
    Double height(@NotNull Entity entity);
}
