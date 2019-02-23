package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Vanilla code adapted to allow reinforced obsidian
 * Borrowed from BlockPortal$Size
 */
public class PortalSize
{
	private final World world;
	private final EnumFacing.Axis axis;
	private final EnumFacing rightDir;
	private final EnumFacing leftDir;
	private int portalBlockCount;
	private BlockPos bottomLeft;
	private int height;
	private int width;

	public PortalSize(World world, BlockPos pos, EnumFacing.Axis axis)
	{
		this.world = world;
		this.axis = axis;

		if (axis == EnumFacing.Axis.X)
		{
			this.leftDir = EnumFacing.EAST;
			this.rightDir = EnumFacing.WEST;
		}
		else
		{
			this.leftDir = EnumFacing.NORTH;
			this.rightDir = EnumFacing.SOUTH;
		}

		for (BlockPos blockPos = pos; pos.getY() > blockPos.getY() - 21 && pos.getY() > 0 && this.isEmptyBlock(world.getBlockState(pos.down()).getBlock()); pos = pos.down())
		{
			;
		}

		int distanceToEdge = this.getDistanceUntilEdge(pos, this.leftDir) - 1;

		if (distanceToEdge >= 0)
		{
			this.bottomLeft = pos.offset(this.leftDir, distanceToEdge);
			this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);

			if (this.width < 2 || this.width > 21)
			{
				this.bottomLeft = null;
				this.width = 0;
			}
		}

		if (this.bottomLeft != null)
		{
			this.height = this.calculatePortalHeight();
		}
	}

	protected int getDistanceUntilEdge(BlockPos pos, EnumFacing facing)
	{
		int distance;

		for (distance = 0; distance < 22; ++distance)
		{
			BlockPos blockPos = pos.offset(facing, distance);

			if (!this.isEmptyBlock(this.world.getBlockState(blockPos).getBlock()) || this.world.getBlockState(blockPos.down()).getBlock() != SCContent.reinforcedObsidian)
			{
				break;
			}
		}

		Block block = this.world.getBlockState(pos.offset(facing, distance)).getBlock();
		return block == SCContent.reinforcedObsidian ? distance : 0;
	}

	public int getHeight()
	{
		return this.height;
	}

	public int getWidth()
	{
		return this.width;
	}

	protected int calculatePortalHeight()
	{
		label56:

			for (this.height = 0; this.height < 21; ++this.height)
			{
				for (int i = 0; i < this.width; ++i)
				{
					BlockPos pos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
					Block block = this.world.getBlockState(pos).getBlock();

					if (!this.isEmptyBlock(block))
					{
						break label56;
					}

					if (block == Blocks.NETHER_PORTAL)
					{
						++this.portalBlockCount;
					}

					if (i == 0)
					{
						block = this.world.getBlockState(pos.offset(this.leftDir)).getBlock();

						if (block != SCContent.reinforcedObsidian)
						{
							break label56;
						}
					}
					else if (i == this.width - 1)
					{
						block = this.world.getBlockState(pos.offset(this.rightDir)).getBlock();

						if (block != SCContent.reinforcedObsidian)
						{
							break label56;
						}
					}
				}
			}

	for (int j = 0; j < this.width; ++j)
	{
		if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != SCContent.reinforcedObsidian)
		{
			this.height = 0;
			break;
		}
	}

	if (this.height <= 21 && this.height >= 3)
	{
		return this.height;
	}
	else
	{
		this.bottomLeft = null;
		this.width = 0;
		this.height = 0;
		return 0;
	}
	} //end method

	protected boolean isEmptyBlock(Block block)
	{
		return block.getMaterial(block.getDefaultState()) == Material.AIR || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
	}

	public boolean isValid()
	{
		return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
	}

	public void placePortalBlocks()
	{
		for (int i = 0; i < this.width; ++i)
		{
			BlockPos pos = this.bottomLeft.offset(this.rightDir, i);

			for (int j = 0; j < this.height; ++j)
			{
				this.world.setBlockState(pos.up(j), Blocks.NETHER_PORTAL.getDefaultState().with(BlockPortal.AXIS, this.axis), 2 | 16);
			}
		}
	}

	public int getPortalBlockCount()
	{
		return portalBlockCount;
	}
}