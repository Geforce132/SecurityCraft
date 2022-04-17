package net.geforcemods.securitycraft.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Marks a block as being able to be hacked with the Codebreaker.
 * 
 * @author Geforce
 */
public interface ICodebreakable {
	/**
	 * Called when a Codebreaker is used on a block.
	 *
	 * @param state The block state of the block.
	 * @param player The player who used the Codebreaker.
	 * @return Return true if the Codebreaker "hack" was successful, false otherwise.
	 */
	public boolean onCodebreakerUsed(BlockState state, Player player);
}
