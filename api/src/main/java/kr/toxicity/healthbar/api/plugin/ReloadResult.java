package kr.toxicity.healthbar.api.plugin;

import org.jetbrains.annotations.NotNull;

public record ReloadResult(@NotNull ReloadState state, long time) {
}
