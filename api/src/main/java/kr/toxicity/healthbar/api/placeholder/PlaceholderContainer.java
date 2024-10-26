package kr.toxicity.healthbar.api.placeholder;

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class PlaceholderContainer<T> {
    private final @NotNull Class<T> clazz;
    private final @NotNull Function<String, T> parser;
    private final @NotNull Function<T, String> stringMapper;

    public static final Pattern PATTERN = Pattern.compile("^(\\((?<type>([a-zA-Z]+))\\))?((?<name>(([a-zA-Z]|\\(|\\)|[0-9]|_|\\.)+))(:(?<argument>([a-zA-Z]|[0-9]|_|\\.|,|)+))?)$");
    private static final Map<Class<?>, PlaceholderContainer<?>> CLASS_MAP = new HashMap<>();
    private static final Map<String, PlaceholderContainer<?>> STRING_MAP = new HashMap<>();

    public PlaceholderContainer(@NotNull Class<T> clazz, String name, @NotNull Function<String, T> parser, @NotNull Function<T, String> stringMapper) {
        this.clazz = clazz;
        this.parser = parser;
        this.stringMapper = stringMapper;

        CLASS_MAP.put(clazz, this);
        STRING_MAP.put(name, this);
    }

    public static final PlaceholderContainer<Number> NUMBER = new PlaceholderContainer<>(
            Number.class,
            "number",
            d -> {
                try {
                    return Double.parseDouble(d);
                } catch (Exception e) {
                    return null;
                }
            },
            s -> DecimalFormat.getInstance().format(s)
    );
    public static final PlaceholderContainer<String> STRING = new PlaceholderContainer<>(
            String.class,
            "string",
            d -> {
                var charArray = d.toCharArray();
                if (charArray.length > 1) {
                    if (charArray[0] == '\'' && charArray[charArray.length - 1] == '\'') return d.substring(1, d.length() - 1);
                }
                return null;
            },
            d -> d
    );
    public static final PlaceholderContainer<Boolean> BOOL = new PlaceholderContainer<>(
            Boolean.class,
            "boolean",
            s -> switch (s) {
                case "true" -> true;
                case "false" -> false;
                default -> null;
            },
            s -> Boolean.toString(s)
    );

    private final Map<String, PlaceholderBuilder<T>> map = new HashMap<>();
    
    public void addPlaceholder(@NotNull String name, @NotNull Function<HealthBarCreateEvent, T> function) {
        map.put(name, new PlaceholderBuilder<>() {
            @Override
            public int requiredArgsCount() {
                return 0;
            }

            @Override
            public @NotNull HealthBarPlaceholder<T> build(@NotNull List<String> strings) {
                return new HealthBarPlaceholder<>() {
                    @NotNull
                    @Override
                    public T value(@NotNull HealthBarCreateEvent player) {
                        return function.apply(player);
                    }

                    @NotNull
                    @Override
                    public Class<T> type() {
                        return clazz;
                    }
                };
            }
        });
    }
    public void addPlaceholder(@NotNull String name, @NotNull PlaceholderBuilder<T> builder) {
        map.put(name, builder);
    }
    private @NotNull FindResult find(@NotNull String name) {
        return new FindResult(name);
    }

    private class FindResult {
        private final PlaceholderBuilder<T> result;
        private FindResult(@NotNull String name) {
            result = map.get(name);
        }

        public @NotNull HealthBarPlaceholder<T> value(@NotNull List<String> strings) {
            Objects.requireNonNull(result);
            Objects.requireNonNull(strings);
            return result.build(strings);
        }

        public boolean ifPresented() {
            return result != null;
        }

        public @NotNull HealthBarPlaceholder<String> stringValue(@NotNull List<String> strings) {
            Objects.requireNonNull(result);
            Objects.requireNonNull(strings);
            var apply = result.build(strings);
            return new HealthBarPlaceholder<>() {
                @NotNull
                @Override
                public Class<String> type() {
                    return String.class;
                }

                @NotNull
                @Override
                public String value(@NotNull HealthBarCreateEvent player) {
                    return stringMapper.apply(apply.value(player));
                }
            };
        }
    }

    private record PrimitivePlaceholder(@NotNull Class<Object> refer, Object value) implements HealthBarPlaceholder<Object> {

        @Override
        public @NotNull Class<Object> type() {
            return refer;
        }

        @Override
        public @NotNull Object value(@NotNull HealthBarCreateEvent player) {
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    public static HealthBarPlaceholder<?> primitive(@NotNull String value) {
        return CLASS_MAP.values().stream().map(c -> {
            var applied = c.parser.apply(value);
            return applied != null ? new PrimitivePlaceholder((Class<Object>) c.clazz, applied) : null;
        }).filter(Objects::nonNull).findFirst().orElseThrow(() -> new RuntimeException("Unable to parse this value: " + value));
    }

    public static HealthBarPlaceholder<?> parse(@NotNull String pattern) {
        var matcher = PATTERN.matcher(pattern);
        if (!matcher.find()) return primitive(pattern);
        var type = matcher.group("type");
        var cast = type != null ? Objects.requireNonNull(STRING_MAP.get(type), "Unsupported type: " + type) : null;
        var name = matcher.group("name");

        var get = CLASS_MAP.values().stream().map(c -> c.find(name)).filter(f -> f.ifPresented()).findFirst().orElse(null);
        if (get == null) return primitive(name);


        var argument = matcher.group("argument");
        var list = argument != null ? Arrays.asList(argument.split(",")) : Collections.<String>emptyList();
        if (get.result.requiredArgsCount() > list.size()) throw new RuntimeException("This placeholder requires argument sized at least " + get.result.requiredArgsCount());
        if (cast != null) {
            var string = get.stringValue(list);
            return new HealthBarPlaceholder<>() {
                @NotNull
                @Override
                @SuppressWarnings("unchecked")
                public Class<Object> type() {
                    return (Class<Object>) cast.clazz;
                }

                @NotNull
                @Override
                public Object value(@NotNull HealthBarCreateEvent player) {
                    return cast.parser.apply(string.value(player));
                }
            };
        } else return get.value(list);
    }

    public static @NotNull Function<HealthBarCreateEvent, Component> toString(@NotNull String pattern) {
        var array = new ArrayList<Function<HealthBarCreateEvent, Component>>();
        var sb = new StringBuilder();
        var skip = false;
        for (char c : pattern.toCharArray()) {
            if (!skip) switch (c) {
                case '[' -> {
                    var string = legacyAdapt(sb.toString());
                    array.add(p -> string);
                    sb.setLength(0);
                }
                case ']' -> {
                    var result = sb.toString();
                    var name = subString(result);
                    var argument = result.length() > name.length() + 1 ? Arrays.asList(result.substring(name.length() + 1).split(",")) : Collections.<String>emptyList();
                    var find = CLASS_MAP.values().stream().map(f -> f.find(name)).filter(f -> f.ifPresented()).findFirst().orElseThrow(() -> new RuntimeException("Unable to find this placeholder: " + name)).stringValue(argument);
                    array.add(f -> legacyAdapt(find.value(f)));
                    sb.setLength(0);
                }
                case '\\' -> skip = true;
                default -> sb.append(c);
            } else sb.append(c);
        }
        if (!sb.isEmpty()) {
            var string = Component.text(sb.toString());
            array.add(p -> string);
            sb.setLength(0);
        }
        return p -> {
            var sb2 = Component.text();
            array.forEach(f -> sb2.append(f.apply(p)));
            return sb2.build();
        };
    }

    private static @NotNull Component legacyAdapt(@NotNull String string) {
        var sb1 = new StringBuilder();
        var sb2 = Component.text();
        var skip = false;
        for (char c : string.toCharArray()) {
            if (!skip) switch (c) {
                case '<' -> {
                    sb2.append(legacyAdapt0(sb1.toString()));
                    sb1.setLength(0);
                    sb1.append(c);
                }
                case '>' -> {
                    sb1.append(c);
                    sb2.append(Component.text(sb1.toString()));
                    sb1.setLength(0);
                }
                case '\\' -> {
                    sb1.append(c);
                    skip = true;
                }
                default -> sb1.append(c);
            } else sb1.append(c);
        }
        if (!sb1.isEmpty()) sb2.append(legacyAdapt0(sb1.toString()));
        return sb2.build();
    }

    private static final TextReplacementConfig TO_MINI_MESSAGE = TextReplacementConfig.builder()
            .match(Pattern.compile(".+"))
            .replacement((r, b) -> MiniMessage.miniMessage().deserialize(r.group()))
            .build();

    private static @NotNull Component legacyAdapt0(@NotNull String s) {
        return LegacyComponentSerializer.legacySection().deserialize(s).replaceText(TO_MINI_MESSAGE);
    }

    private static @NotNull String subString(@NotNull String string) {
        var sb = new StringBuilder();
        for (char c1 : string.toCharArray()) {
            if (':' != c1) sb.append(c1);
            else break;
        }
        return sb.toString();
    }
}
