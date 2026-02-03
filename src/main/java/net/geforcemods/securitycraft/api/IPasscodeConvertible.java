package net.geforcemods.securitycraft.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Defines a block that can be converted to a passcode-protected variant by rightclicking it with a Key Panel. Call
 *
 * <pre>
 * InterModComms.sendTo("securitycraft", SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, ClassThatImplementsIPasscodeConvertible::new);
 * </pre>
 *
 * during InterModEnqueueEvent to register this with SecurityCraft. <p> - SecurityCraft already comes with a few built-in
 * implementations, for example for barrels, chests, or furnaces.
 *
 * @author bl4ckscor3
 */
public interface IPasscodeConvertible {
	/**
	 * Checks whether the given block state is valid for converting to a passcode protected block with this implementation
	 *
	 * @param state The block state to check
	 * @return true if the block can be used for this conversion, false otherwise
	 */
	public boolean isUnprotectedBlock(BlockState state);

	/**
	 * Checks whether the given block state is a passcode protected block that can be converted to an unprotected form with this
	 * implementation
	 *
	 * @param state The block state to check
	 * @return true if the block can be used for this conversion, false otherwise
	 */
	public boolean isProtectedBlock(BlockState state);

	/**
	 * Converts the original block to the passcode-protected one
	 *
	 * @param player The player who initiated the conversion
	 * @param level The level in which the conversion takes place
	 * @param pos The position the conversion takes place at
	 * @return true if the conversion was successful, false otherwise
	 */
	public boolean protect(Player player, Level level, BlockPos pos);

	/**
	 * Converts the passcode-protected block to the original one
	 *
	 * @param player The player who initiated the conversion
	 * @param level The level in which the conversion takes place
	 * @param pos The position the conversion takes place at
	 * @return true if the conversion was successful, false otherwise
	 */
	public boolean unprotect(Player player, Level level, BlockPos pos);

	/**
	 * Returns the amount of key panel items that are consumed when converting the given state to a passcode-protected block
	 *
	 * @param state The block state to check
	 * @return The amount of key panel items that will be consumed on conversion
	 */
	public default int getRequiredKeyPanels(BlockState state) {
		return 1;
	}
}
