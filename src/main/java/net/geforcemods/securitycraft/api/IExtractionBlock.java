package net.geforcemods.securitycraft.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Defines a block that can extract from a password-protected chest and furnace.
 * Call <pre>InterModComms.sendTo("securitycraft", "registerExtractionBlock", ClassThatImplementsIExtractionBlock::new);</pre>
 * to register this with SecurityCraft.
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
	public boolean canExtract(IOwnable te, World world, BlockPos pos, BlockState state);

	/**
	 * @return The block that is trying to extract from a password-protected chest/furnace
	 */
	public Block getBlock();
}
