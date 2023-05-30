package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Defines a block that can be converted to a passcode-protected variant by rightclicking it with a Key Panel. Call
 *
 * <pre>
 * InterModComms.sendTo("securitycraft", SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, ClassThatImplementsIPasscodeConvertible::new);
 * </pre>
 *
 * during InterModEnqueueEvent to register this with SecurityCraft. <p> - If you are converting a chest into a
 * passcode-protected chest, you can extend {@link KeypadChestBlock.Convertible}.<br> - If you are converting a furnace into
 * a passcode-protected furnace, you can extend {@link AbstractKeypadFurnaceBlock.Convertible}.
 *
 * @author bl4ckscor3
 */
public interface IPasscodeConvertible {
	/**
	 * Checks whether the given block state is valid for this conversion
	 *
	 * @param state The block state to check
	 * @return true if the block can be used for this conversion, false otherwise
	 */
	public boolean isValidStateForConversion(BlockState state);

	/**
	 * Converts the original block to the passcode-protected one
	 *
	 * @param player The player who initiated the conversion
	 * @param world The world in which the conversion takes place
	 * @param pos The position the conversaion takes place at
	 * @return true if the conversion was successful, false otherwise
	 */
	public boolean convert(PlayerEntity player, World world, BlockPos pos);
}
