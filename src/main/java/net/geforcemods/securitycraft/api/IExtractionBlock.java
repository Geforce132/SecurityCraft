package net.geforcemods.securitycraft.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Defines a block that can extract from a Password-protected Chest, Password-protected Furnace, and Block Pocket Manager.
 * Call <pre>InterModComms.sendTo("securitycraft", SecurityCraftAPI.IMC_EXTRACTION_BLOCK_MSG, ClassThatImplementsIExtractionBlock::new);</pre>
 * during InterModEnqueueEvent to register this with SecurityCraft.
 *
 * @author bl4ckscor3
 */
public interface IExtractionBlock
{
	/**
	 * The protected block uses this to check if this block can extract items
	 *
	 * @param te The tile entity of the protected block
	 * @param world The world that the protected block is in
	 * @param pos The position of the block that is trying to extract items
	 * @param state The state of the block that is trying to extract items
	 * @return true if extraction is possible, false otherwise
	 */
	public boolean canExtract(IOwnable te, Level world, BlockPos pos, BlockState state);

	/**
	 * @return The block that is trying to extract from a password-protected chest/furnace
	 */
	public Block getBlock();
}
