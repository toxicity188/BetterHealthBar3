package kr.toxicity.healthbar.api;

import kr.toxicity.healthbar.api.bedrock.BedrockAdapter;
import kr.toxicity.healthbar.api.manager.*;
import kr.toxicity.healthbar.api.modelengine.ModelEngineAdapter;
import kr.toxicity.healthbar.api.nms.NMS;
import kr.toxicity.healthbar.api.plugin.ReloadResult;
import kr.toxicity.healthbar.api.scheduler.WrappedScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.BiConsumer;

public abstract class BetterHealthBar extends JavaPlugin {

    public static final String NAMESPACE = "betterhealthbar";

    private static BetterHealthBar instance;
    @Override
    public final void onLoad() {
        if (instance != null) throw new RuntimeException();
        instance = this;
    }
    public static @NotNull BetterHealthBar inst() {
        return Objects.requireNonNull(instance, "BetterHealthBar is not loaded!");
    }

    public abstract @NotNull ReloadResult reload();
    public abstract boolean onReload();
    public abstract @NotNull NMS nms();
    public abstract boolean isPaper();
    public abstract boolean isFolia();
    public abstract @NotNull WrappedScheduler scheduler();
    public abstract @NotNull ModelEngineAdapter modelEngine();
    public abstract @NotNull BedrockAdapter bedrock();
    public abstract void loadAssets(@NotNull String prefix, @NotNull BiConsumer<String, InputStream> consumer);
    public abstract void loadAssets(@NotNull String prefix, @NotNull File dir);

    public abstract @NotNull ConfigManager configManager();
    public abstract @NotNull ImageManager imageManager();
    public abstract @NotNull PlayerManager playerManager();
    public abstract @NotNull LayoutManager layoutManager();
    public abstract @NotNull HealthBarManager healthBarManager();
    public abstract @NotNull ListenerManager listenerManager();
}
