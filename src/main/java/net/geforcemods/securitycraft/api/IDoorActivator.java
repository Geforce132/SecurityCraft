package net.geforcemods.securitycraft.api;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
	 * @param be The block entity of the block that could power the doors, if it has one. null if there is no tile entity present
	 * @param direction The direction the block that could power the doors is in, relative to the doors
	 * @param distance The amount of blocks that the block that could power the doors is away from the door
	 * @return true if the door should get powered, false otherwise
	 */
	public boolean isPowering(Level world, BlockPos pos, BlockState state, BlockEntity be, Direction direction, int distance);

	/**
	 * @return The block(s) that this IDoorActivator defines as being able to activate the doors
	 */
	public List<Block> getBlocks();
}
