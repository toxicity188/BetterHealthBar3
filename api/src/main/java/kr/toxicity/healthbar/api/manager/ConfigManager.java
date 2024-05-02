package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.pack.PackType;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface ConfigManager {
    boolean debug();
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
}
