package io.github.invvk.mony.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerComboMobEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Entity mob;

    private final int amountOfHits;

    private final int originalAmount;

    private double newAmount;

    private double multiplier;

    private boolean cancelled;

    public PlayerComboMobEvent(Player player, Entity mob, int amountOfHits, int originalAmount, double newAmount, double multiplier) {
        super(player);
        this.mob = mob;
        this.amountOfHits = amountOfHits;
        this.originalAmount = originalAmount;
        this.newAmount = newAmount;
        this.multiplier = multiplier;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * set the new amount of money to be given to the player
     * @param newAmount new amount of money
     */
    public void setNewAmount(double newAmount) {
        this.newAmount = newAmount;
    }

    /**
     * set money multiplier for the player
     * this will result in recalculation for the new amount of money to be given<br><br>
     *
     * {@code new amount = (original amount * new multiplier) + originalAmount;}
     * @param multiplier new multiplier
     */
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
        this.newAmount = (originalAmount * multiplier) + originalAmount;
    }

    /**
     *
     * @return Mob that the player has killed
     */
    public Entity getMob() {
        return this.mob;
    }

    /**
     * Amount of hits in the combo duration
     * @return amount of hits that resulted in the combo
     */
    public int getAmountOfHits() {
        return this.amountOfHits;
    }

    /**
     * Original amount of money to be given before the combo multiplier applied
     * @return original amount of money to be given
     */
    public int getOriginalAmount() {
        return this.originalAmount;
    }

    /**
     * Money after the combo multiplier added to the original amount
     * @return new amount = (original amount * new multiplier) + originalAmount;
     */
    public double getNewAmount() {
        return this.newAmount;
    }

    /**
     *
     * @return the combo multiplier
     */
    public double getMultiplier() {
        return this.multiplier;
    }
}
