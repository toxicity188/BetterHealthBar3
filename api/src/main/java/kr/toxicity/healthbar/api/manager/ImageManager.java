package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.image.HealthBarImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ImageManager {
    @Nullable
    HealthBarImage image(@NotNull String name);
}
