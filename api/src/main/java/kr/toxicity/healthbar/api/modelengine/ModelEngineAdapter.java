package kr.toxicity.healthbar.api.modelengine;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface ModelEngineAdapter {
    ModelEngineAdapter NONE = e -> 0;
    double getHeight(@NotNull Entity entity);
}
