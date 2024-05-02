package kr.toxicity.healthbar.api.layout;

import kr.toxicity.healthbar.api.component.PixelComponent;
import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import kr.toxicity.healthbar.api.image.HealthBarImage;
import kr.toxicity.healthbar.api.listener.HealthBarListener;
import kr.toxicity.healthbar.api.renderer.ImageRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ImageLayout extends Layout, Iterable<PixelComponent> {
    @NotNull
    HealthBarImage image();
    @NotNull
    @Unmodifiable
    List<PixelComponent> components();
    @NotNull
    HealthBarListener listener();
    int duration();

    @NotNull
    ImageRenderer createImageRenderer(@NotNull HealthBarEntity entity);
}
