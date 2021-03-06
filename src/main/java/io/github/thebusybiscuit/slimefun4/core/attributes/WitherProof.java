package io.github.thebusybiscuit.slimefun4.core.attributes;

import org.bukkit.block.Block;
import org.bukkit.entity.Wither;

import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.WitherProofBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

/**
 * This Interface, when attached to a class that inherits from {@link SlimefunItem}, marks
 * the Item as "Wither-Proof".
 * Wither-Proof blocks cannot be destroyed by a {@link Wither}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see WitherProofBlock
 *
 */
@FunctionalInterface
public interface WitherProof extends ItemAttribute {

    /**
     * This method is called when a {@link Wither} tried to attack the given {@link Block}.
     * You can use this method to play particles or even damage the {@link Wither}.
     * 
     * @param block
     *            The {@link Block} which was attacked.
     * @param wither
     *            The {@link Wither} who attacked.
     */
    void onAttack(Block block, Wither wither);

}
