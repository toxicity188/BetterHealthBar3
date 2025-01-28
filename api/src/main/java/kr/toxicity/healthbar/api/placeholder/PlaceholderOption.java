package kr.toxicity.healthbar.api.placeholder;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum PlaceholderOption {
    NUMBER_FORMAT("#,###", ConfigurationSection::getString),
    UNCOLORED(false, ConfigurationSection::getBoolean)
    ;
    private final @NotNull Object defaultValue;
    private final BiFunction<ConfigurationSection, String, Object> mapper;

    public static final Property EMPTY = new Property(Arrays.stream(values()).collect(Collectors.toMap(v -> v, v -> v.defaultValue)));

    public static @NotNull Property of(@NotNull ConfigurationSection section) {
        return new Property(Arrays.stream(values()).collect(Collectors.toMap(v -> v, v -> {
            var get = v.mapper.apply(section, v.configName());
            return get != null ? get : v.defaultValue;
        })));
    }

    public @NotNull String configName() {
        return name().toLowerCase().replace("_", "-");
    }

    @RequiredArgsConstructor
    public static final class Property {
        private final Map<PlaceholderOption, Object> map;

        public @Nullable Object get(@NotNull PlaceholderOption option) {
            return map.get(option);
        }
    }
}
