package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.EntityFallingOwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockFullMineFalling extends BlockFullMineBase
{
	public static boolean fallInstantly;

	public BlockFullMineFalling(Material material, Block disguisedBlock)
	{
		super(material, disguisedBlock);
	}

	/**
	 * Called after the block is set in the Chunk data, but before the Tile Entity is set
	 */
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}

	/**
	 * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
	 * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
	 * block, etc.
	 */
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}

	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(!world.isRemote)
		{
			if(canFallInto(world, pos.down()) && pos.getY() >= 0)
			{
				if(!fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
				{
					if(!world.isRemote && world.getTileEntity(pos) instanceof IOwnable)
						world.spawnEntityInWorld(new EntityFallingOwnableBlock(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos), ((IOwnable)world.getTileEntity(pos)).getOwner()));
				}
				else
				{
					BlockPos blockpos;

					world.setBlockToAir(pos);

					for(blockpos = pos.down(); canFallInto(world, blockpos) && blockpos.getY() > 0; blockpos = blockpos.down()){}

					if(blockpos.getY() > 0)
						world.setBlockState(blockpos.up(), this.getDefaultState());
				}
			}
		}
	}

	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World world)
	{
		return 2;
	}

	public static boolean canFallInto(World world, BlockPos pos)
	{
		if(world.isAirBlock(pos))
			return true;

		Block block = world.getBlockState(pos).getBlock();
		Material material = block.getMaterial();

		return block == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava;
	}
}
