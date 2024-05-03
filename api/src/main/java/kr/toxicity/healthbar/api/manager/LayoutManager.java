package kr.toxicity.healthbar.api.manager;

import kr.toxicity.healthbar.api.layout.LayoutGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface LayoutManager {
    @Nullable
    LayoutGroup name(@NotNull String name);

    @NotNull
    @Unmodifiable
    List<LayoutGroup> group(@NotNull String group);
}
