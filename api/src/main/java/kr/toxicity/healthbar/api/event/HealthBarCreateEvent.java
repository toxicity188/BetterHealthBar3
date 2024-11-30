package kr.toxicity.healthbar.api.event;

import kr.toxicity.healthbar.api.entity.HealthBarEntity;
import kr.toxicity.healthbar.api.healthbar.HealthBar;
import kr.toxicity.healthbar.api.player.HealthBarPlayer;
import kr.toxicity.healthbar.api.trigger.HealthBarTrigger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

@Getter
@Setter
public class HealthBarCreateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull HealthBar healthBar;
    private final @NotNull HealthBarTrigger trigger;
    private final @NotNull HealthBarPlayer player;
    private final @NotNull HealthBarEntity entity;

    private boolean cancelled;

    private Predicate<HealthBarCreateEvent> predicate = e -> true;

    public HealthBarCreateEvent(@NotNull HealthBar healthBar, @NotNull HealthBarTrigger trigger, @NotNull HealthBarPlayer player, @NotNull HealthBarEntity entity) {
        super(true);
        this.healthBar = healthBar;
        this.trigger = trigger;
        this.player = player;
        this.entity = entity;
    }

    public @NotNull HealthBarCreateEvent addPredicate(@NotNull Predicate<HealthBarCreateEvent> other) {
        predicate = predicate.and(other);
        return this;
    }

    public boolean check() {
        return predicate.test(this);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
