package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.configuration.CoreShadersOption;
import kr.toxicity.healthbar.api.pack.PackType;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.Set;

public interface ConfigManager {
    boolean debug();
    boolean metrics();
    @NotNull
    PackType packType();
    @NotNull
    File buildFolder();
    @NotNull
    String namespace();
    int defaultDuration();
    double defaultHeight();
    double lookDegree();
    double lookDistance();
    @NotNull
    @Unmodifiable
    Set<String> mergeOtherFolder();
    boolean createPackMcmeta();
    boolean enableSelfHost();
    int selfHostPort();
    @NotNull
    @Unmodifiable
    Set<EntityType> blacklistEntityType();
    boolean disableToInvulnerableMob();
    @NotNull
    CoreShadersOption shaders();
}
