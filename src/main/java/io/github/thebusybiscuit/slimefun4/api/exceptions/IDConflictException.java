package io.github.thebusybiscuit.slimefun4.api.exceptions;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

/**
 * An {@link IDConflictException} is thrown whenever two Addons try to add
 * a {@link SlimefunItem} with the same id.
 * 
 * @author TheBusyBiscuit
 *
 */
public class IDConflictException extends RuntimeException {

    private static final long serialVersionUID = -733012666374895255L;

    /**
     * Constructs a new {@link IDConflictException} with the given items.
     * 
     * @param item1
     *            The first {@link SlimefunItem} with this id
     * @param item2
     *            The second {@link SlimefunItem} with this id
     */
    public IDConflictException(SlimefunItem item1, SlimefunItem item2) {
        super("Two items have conflicting ids: " + item1.toString() + " and " + item2.toString());
    }

}