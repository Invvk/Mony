package io.github.invvk.mony.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerKillMobEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final Entity mob;

    @Getter private final EntityType type;

    @Getter @Setter private double amount;

    @Getter private final double originalAmount;

    @Getter @Setter
    private boolean cancelled = false;

    public PlayerKillMobEvent(Player player, Entity mob, double amount) {
        super(player);
        this.mob = mob;
        this.type = mob.getType();
        this.amount = amount;
        this.originalAmount = amount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
