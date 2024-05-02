package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.layout.LayoutGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LayoutManager {
    @Nullable
    LayoutGroup group(@NotNull String name);
}
