package kr.toxicity.healthbar.api.scheduler;

import org.jetbrains.annotations.NotNull;

public interface WrappedScheduler {
    @NotNull WrappedTask task(@NotNull Runnable runnable);
    @NotNull WrappedTask asyncTask(@NotNull Runnable runnable);
    @NotNull WrappedTask asyncTaskTimer(long delay, long period, @NotNull Runnable runnable);
}
