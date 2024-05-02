package kr.toxicity.healthbar.api.image;

import kr.toxicity.healthbar.api.configuration.HealthBarConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface HealthBarImage extends HealthBarConfiguration {
    @NotNull
    @Unmodifiable
    List<NamedProcessedImage> images();
    @NotNull
    ImageType type();
}
