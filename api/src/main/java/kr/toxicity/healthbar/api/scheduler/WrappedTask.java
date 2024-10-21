package kr.toxicity.healthbar.api.scheduler;

@FunctionalInterface
public interface WrappedTask {
    void cancel();
}
