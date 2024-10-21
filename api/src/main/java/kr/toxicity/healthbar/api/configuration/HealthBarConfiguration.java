package kr.toxicity.healthbar.api.configuration;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface HealthBarConfiguration {
    @NotNull
    String path();
}
