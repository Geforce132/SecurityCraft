package net.geforcemods.securitycraft.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Marks a block as being able to be hacked with the Codebreaker.
 *
 * @author Geforce
 */
public interface ICodebreakable {
	/**
	 * Checked before any codebreaking attempt, whether the codebreaker should attempt to break the code. Useful when the block
	 * currently does not accept a code at all.
	 *
	 * @param state The state of the block that the codebreaking attempt should be performed on
	 * @param player The player trying the codebreaking attempt
	 * @return true if the codebreaking attempt should be performed, false otherwise
	 */
	public boolean shouldAttemptCodebreak(IBlockState state, EntityPlayer player);

	/**
	 * Called when a Codebreaker has successfully broken the code of a block.
	 *
	 * @param state The block state of the block.
	 * @param player The player who used the Codebreaker.
	 */
	public void useCodebreaker(IBlockState state, EntityPlayer player);
}
