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

@Getter
@Setter
public class HealthBarCreateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull HealthBar healthBar;
    private final @NotNull HealthBarTrigger trigger;
    private final @NotNull HealthBarPlayer player;
    private final @NotNull HealthBarEntity entity;

    private boolean cancelled;

    public HealthBarCreateEvent(@NotNull HealthBar healthBar, @NotNull HealthBarTrigger trigger, @NotNull HealthBarPlayer player, @NotNull HealthBarEntity entity) {
        super(true);
        this.healthBar = healthBar;
        this.trigger = trigger;
        this.player = player;
        this.entity = entity;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
