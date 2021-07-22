package net.geforcemods.securitycraft.api;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Defines a block that can activate a Reinforced Doors/Reinforced Trapdoors/Reinforced Fence Gates (henceforth referred to as "the doors").
 * Call <pre>InterModComms.sendTo("securitycraft", SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, ClassThatImplementsIDoorActivator::new);</pre>
 * during InterModEnqueueEvent to register this with SecurityCraft.
 *
 * @author bl4ckscor3
 */
public interface IDoorActivator
{
	/**
	 * @param world The world in which the check takes place
	 * @param pos The position of the block that could power the doors
	 * @param state The state of the block that could power the doors
	 * @param te The tile entity of the block that could power the doors, if it has one. null if there is no tile entity present
	 * @return true if the door should get powered, false otherwise
	 */
	public boolean isPowering(Level world, BlockPos pos, BlockState state, BlockEntity te);

	/**
	 * @return The block(s) that this IDoorActivator defines as being able to activate the doors
	 */
	public List<Block> getBlocks();
}
