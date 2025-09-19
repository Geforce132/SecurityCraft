package net.geforcemods.securitycraft.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Defines a block that can be converted to a passcode-protected variant by rightclicking it with a Key Panel. Call
 *
 * <pre>
 * FMLInterModComms.sendFunctionMessage("securitycraft", SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, "your.package.ClassThatImplementsIPasscodeConvertible");
 * </pre>
 *
 * during FMLInitializationEvent to register this with SecurityCraft.<br> Do note, that you also need to implement
 * Function<Object,IPasscodeConvertible> on the class that you send via IMC. You can just return <code>this</code> in the
 * apply method. The Object argument is unused and will always be null. <p> - SecurityCraft already comes with a few built-in
 * implementations, for example for chests or furnaces.
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
	public boolean isUnprotectedBlock(IBlockState state);

	/**
	 * Checks whether the given block state is a passcode protected block that can be converted to an unprotected form with this
	 * implementation
	 *
	 * @param state The block state to check
	 * @return true if the block can be used for this conversion, false otherwise
	 */
	public boolean isProtectedBlock(IBlockState state);

	/**
	 * Converts the original block to the passcode-protected one
	 *
	 * @param player The player who initiated the conversion
	 * @param level The world in which the conversion takes place
	 * @param pos The position the conversion takes place at
	 * @return true if the conversion was successful, false otherwise
	 */
	public boolean protect(EntityPlayer player, World level, BlockPos pos);

	/**
	 * Converts the passcode-protected block to the original one
	 *
	 * @param player The player who initiated the conversion
	 * @param level The level in which the conversion takes place
	 * @param pos The position the conversion takes place at
	 * @return true if the conversion was successful, false otherwise
	 */
	public boolean unprotect(EntityPlayer player, World level, BlockPos pos);

	/**
	 * Returns the amount of key panel items that are consumed when converting the given state to a passcode-protected block
	 *
	 * @param state The block state to check
	 * @param level The level in which the conversion takes place
	 * @param pos The position the conversion takes place at
	 * @return The amount of key panel items that will be consumed on conversion
	 */
	public default int getRequiredKeyPanels(IBlockState state, World level, BlockPos pos) {
		return 1;
	}
}
