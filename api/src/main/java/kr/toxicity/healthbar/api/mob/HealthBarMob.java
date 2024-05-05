package kr.toxicity.healthbar.api.mob;

import org.jetbrains.annotations.NotNull;

public interface HealthBarMob {
    @NotNull String id();
    @NotNull Object handle();
    @NotNull MobConfiguration configuration();
}
