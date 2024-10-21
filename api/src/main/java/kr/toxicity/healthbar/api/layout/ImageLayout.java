package kr.toxicity.healthbar.api.layout;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import kr.toxicity.healthbar.api.image.HealthBarImage;
import kr.toxicity.healthbar.api.listener.HealthBarListener;
import kr.toxicity.healthbar.api.renderer.ImageRenderer;
import org.jetbrains.annotations.NotNull;

public interface ImageLayout extends Layout {
    @NotNull
    HealthBarImage image();
    @NotNull
    HealthBarListener listener();
    int duration();

    @NotNull
    ImageRenderer createImageRenderer(@NotNull HealthBarCreateEvent entity);
}
