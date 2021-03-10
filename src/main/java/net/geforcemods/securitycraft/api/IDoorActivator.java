package net.geforcemods.securitycraft.api;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Defines a block that can activate a Reinforced Doors/Reinforced Trapdoors/Reinforced Fence Gates (henceforth referred to as "the doors").
 * Call <pre>FMLInterModComms.sendFunctionMessage("securitycraft", SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, "your.package.ClassThatImplementsIDoorActivator");</pre>
 * during InterModEnqueueEvent to register this with SecurityCraft.<br>
 * Do note, that you also need to implement Function<Object,IDoorActivator> on the class that you send via IMC. You can just return <code>this</code>
 * in the apply method. The Object argument is unused and will always be null.
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
	public boolean isPowering(World world, BlockPos pos, IBlockState state, TileEntity te);

	/**
	 * @return The block(s) that this IDoorActivator defines as being able to activate the doors
	 */
	public List<Block> getBlocks();
}
