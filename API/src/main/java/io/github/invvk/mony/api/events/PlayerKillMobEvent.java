package io.github.invvk.mony.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event before money gets deposited in the user account after killing a mob <br> <br>
 * <strong>THIS EVENT IS SYNC</strong>
 *
 * @since 1.0.0
 */
public class PlayerKillMobEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final Entity mob;

    @Getter private final EntityType type;

    @Setter private double amount;

    private final double originalAmount;

    private boolean preventDL;

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

    /**
     * Get the original amount of money to be given to the user
     *
     * @return original amount of money, constant value
     */
    public double getOriginalAmount() {
        return originalAmount;
    }

    /**
     * This represents the modifiable amount of money <br>
     * This will be given to the user.
     * @return modified amount of money
     */
    public double getAmount() {
        return amount;
    }

    /**
     * setting this to true will prevent any modification related to last max amount, and daily limit triggering.
     * <br> <br> However, this WILL NOT TRIGGER IF THE PLAYER IS ON COOLDOWN.
     * @return true if the modification of daily limit should be ignored.
     */
    public boolean isPreventDL() {
        return preventDL;
    }
}
