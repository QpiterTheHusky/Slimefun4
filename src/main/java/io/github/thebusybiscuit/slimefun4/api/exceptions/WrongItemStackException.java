package io.github.thebusybiscuit.slimefun4.api.exceptions;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * A {@link WrongItemStackException} is thrown when someone tries to alter an {@link ItemStack}
 * but actually wanted to alter a different one.
 * 
 * If for example a {@link DamageableItem} accidentally damages the original {@link SlimefunItem}
 * instead of the held {@link ItemStack}, this will be thrown.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunItemStack
 * @see SlimefunItem
 *
 */
public class WrongItemStackException extends RuntimeException {

    private static final long serialVersionUID = 9144658137363309071L;

    /**
     * This constructs a new {@link WrongItemStackException} with the given error context.
     * 
     * @param message
     *            An error message to display
     */
    public WrongItemStackException(String message) {
        super("You probably wanted alter a different ItemStack: " + message);
    }

}
