package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
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
 * apply method. The Object argument is unused and will always be null. <p> - If you are converting a chest into a
 * passcode-protected chest, you can extend {@link KeypadChestBlock.Convertible}.<br> - If you are converting a furnace into
 * a passcode-protected furnace, you can extend {@link KeypadFurnaceBlock.Convertible}.
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
	public boolean isValidStateForConversion(IBlockState state);

	/**
	 * Converts the original block to the passcode-protected one
	 *
	 * @param player The player who initiated the conversion
	 * @param world The world in which the conversion takes place
	 * @param pos The position the conversaion takes place at
	 * @return true if the conversion was successful, false otherwise
	 */
	public boolean convert(EntityPlayer player, World world, BlockPos pos);
}
