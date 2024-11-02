package kr.toxicity.healthbar.api.placeholder;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum PlaceholderOption {
    NUMBER_FORMAT("#,###")
    ;
    private final @NotNull String defaultValue;

    public static final Property EMPTY = new Property(Arrays.stream(values()).collect(Collectors.toMap(v -> v, v -> v.defaultValue)));

    public static @NotNull Property of(@NotNull ConfigurationSection section) {
        return new Property(Arrays.stream(values()).collect(Collectors.toMap(v -> v, v -> section.getString(v.configName(), v.defaultValue))));
    }

    public @NotNull String configName() {
        return name().toLowerCase().replace("_", "-");
    }

    @RequiredArgsConstructor
    public static final class Property {
        private final Map<PlaceholderOption, String> map;

        public @Nullable String get(@NotNull PlaceholderOption option) {
            return map.get(option);
        }
    }
}
