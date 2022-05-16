package io.github.invvk.mony.api.events;

import io.github.invvk.mony.api.database.User;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

/**
 * Async Event called when the user cache gets loaded from {@link AsyncPlayerPreLoginEvent}
 * <br>this event adds {@code MonyCacheLoadEvent.getLoadedUser()} <br> <br>
 * <strong>THIS EVENT IS ASYNC</strong>
 *
 * @since 1.0.0
 * @author Invvk
 */
public class MonyCacheLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final UUID uniqueId;
    @Getter private final String name;
    @Getter private final User loadedUser;

    public MonyCacheLoadEvent(UUID uniqueId, String name, User loadedUser) {
        super(true);
        this.uniqueId = uniqueId;
        this.name = name;
        this.loadedUser = loadedUser;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
