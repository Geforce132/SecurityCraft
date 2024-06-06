package net.geforcemods.securitycraft.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Defines a block that can extract from a Passcode-protected Chest, Passcode-protected Furnace, Block Pocket Manager,
 * Security Sea Boat, ... <p>Call
 *
 * <pre>
 * InterModComms.sendTo("securitycraft", SecurityCraftAPI.IMC_EXTRACTION_BLOCK_MSG, ClassThatImplementsIExtractionBlock::new);
 * </pre>
 *
 * during InterModEnqueueEvent to register this with SecurityCraft.
 *
 * @author bl4ckscor3
 */
public interface IExtractionBlock {
	/**
	 * The protected object uses this to check if this block can extract items
	 *
	 * @param ownable The protected object
	 * @param level The level that the object is in
	 * @param pos The position of the block that is trying to extract items
	 * @param state The state of the block that is trying to extract items
	 * @return true if extraction is possible, false otherwise
	 */
	public boolean canExtract(IOwnable ownable, Level level, BlockPos pos, BlockState state);

	/**
	 * @return The block that is trying to extract from something
	 */
	public Block getBlock();
}
