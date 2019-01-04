package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.EntityFallingOwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockReinforcedFalling extends BlockReinforcedBase
{
	public static boolean fallInstantly;

	public BlockReinforcedFalling(Material material, Block disguisedBlock)
	{
		super(material, 1, disguisedBlock);

		if(material == Material.sand)
			setStepSound(Block.soundTypeSand);
		else
			setStepSound(Block.soundTypeGravel);
	}

	public void onBlockAdded(World world, int x, int y, int z)
	{
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if(!world.isRemote)
		{
			if (canFallBelow(world, x, y - 1, z) && y >= 0)
			{
				if(!fallInstantly && world.checkChunksExist(x - 32, y - 32, z - 32, x + 32, y + 32, z + 32))
				{
					if(!world.isRemote && world.getTileEntity(x, y, z) instanceof IOwnable)
						world.spawnEntityInWorld(new EntityFallingOwnableBlock(world, x + 0.5F, y + 0.5F, z + 0.5F, this, world.getBlockMetadata(x, y, z), ((IOwnable)world.getTileEntity(x, y, z)).getOwner()));
				}
				else
				{
					world.setBlockToAir(x, y, z);

					while(canFallBelow(world, x, y - 1, z) && y > 0)
					{
						--y;
					}

					if(y > 0)
						world.setBlock(x, y, z, this);
				}
			}
		}
	}

	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World worldIn)
	{
		return 2;
	}

	public static boolean canFallBelow(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);

		if(block.isAir(world, x, y, z))
			return true;
		else if(block == Blocks.fire)
			return true;
		else
		{
			Material material = block.getMaterial();

			return material == Material.water ? true : material == Material.lava;
		}
	}
}