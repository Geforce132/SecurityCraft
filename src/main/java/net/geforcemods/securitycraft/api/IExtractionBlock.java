package net.geforcemods.securitycraft.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Defines a block that can extract from a password-protected chest and furnace.
 * Call <pre>FMLInterModComms.sendFunctionMessage("securitycraft", "registerExtractionBlock", "your.package.ClassThatImplementsIExtractionBlock");</pre>
 * during InterModEnqueueEvent to register this with SecurityCraft.
 * Do note, that you also need to implement Function<Object,IExtractionBlock> on the class that you send via IMC. You can just return <code>this</code>
 * in the apply method. The Object argument is unused and will always be null.
 *
 * @author bl4ckscor3
 */
public interface IExtractionBlock
{
	/**
	 * The password-protected chest/furnace use this to check if this block can extract items
	 *
	 * @param te The password-protected chest/furnace
	 * @param world The world that the password-protected chest/furnace is in
	 * @param pos The position of the block that is trying to extract items
	 * @param state The state of the block that is trying to extract items
	 * @return true if extraction is possible, false otherwise
	 */
	public boolean canExtract(IOwnable te, World world, BlockPos pos, IBlockState state);

	/**
	 * @return The block that is trying to extract from a password-protected chest/furnace
	 */
	public Block getBlock();
}
