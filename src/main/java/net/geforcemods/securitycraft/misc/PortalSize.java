package net.geforcemods.securitycraft.misc;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

/**
 * Vanilla code adapted to allow reinforced obsidian
 * Borrowed from NetherPortalBlock$Size
 */
public class PortalSize
{
	private final IWorld world;
	private final Direction.Axis axis;
	private final Direction rightDir;
	private final Direction leftDir;
	private int portalBlockCount;
	@Nullable
	private BlockPos bottomLeft;
	private int height;
	private int width;

	public PortalSize(IWorld world, BlockPos pos, Direction.Axis axis)
	{
		this.world = world;
		this.axis = axis;

		if(axis == Direction.Axis.X)
		{
			leftDir = Direction.EAST;
			rightDir = Direction.WEST;
		}
		else
		{
			leftDir = Direction.NORTH;
			rightDir = Direction.SOUTH;
		}

		for(BlockPos iPos = pos; pos.getY() > iPos.getY() - 21 && pos.getY() > 0 && isPartOfUnfinishedPortal(world.getBlockState(pos.down())); pos = pos.down()) {
			;
		}

		int i = getDistanceUntilEdge(pos, leftDir) - 1;

		if(i >= 0)
		{
			bottomLeft = pos.offset(leftDir, i);
			width = getDistanceUntilEdge(bottomLeft, rightDir);

			if(width < 2 || width > 21)
			{
				bottomLeft = null;
				width = 0;
			}
		}

		if(bottomLeft != null)
			height = calculatePortalHeight();
	}

	protected int getDistanceUntilEdge(BlockPos pos, Direction direction)
	{
		int i;

		for(i = 0; i < 22; ++i)
		{
			BlockPos offsetPos = pos.offset(direction, i);

			if(!isPartOfUnfinishedPortal(world.getBlockState(offsetPos)) || world.getBlockState(offsetPos.down()).getBlock() != SCContent.reinforcedObsidian)
				break;
		}

		Block block = world.getBlockState(pos.offset(direction, i)).getBlock();

		return block == SCContent.reinforcedObsidian ? i : 0;
	}

	protected int calculatePortalHeight()
	{
		{
			loop:
				for(height = 0; height < 21; ++height)
				{
					for(int i = 0; i < width; ++i)
					{
						BlockPos pos = bottomLeft.offset(rightDir, i).up(height);
						BlockState state = world.getBlockState(pos);

						if (!isPartOfUnfinishedPortal(state))
							break loop;

						Block block = state.getBlock();

						if (block == Blocks.NETHER_PORTAL)
							++portalBlockCount;

						if (i == 0)
						{
							block = world.getBlockState(pos.offset(leftDir)).getBlock();

							if (block != SCContent.reinforcedObsidian)
								break loop;
						}
						else if(i == this.width - 1)
						{
							block = world.getBlockState(pos.offset(rightDir)).getBlock();

							if(block != SCContent.reinforcedObsidian)
								break loop;
						}
					}
				}
		}

		for(int j = 0; j < width; ++j)
		{
			if(world.getBlockState(bottomLeft.offset(rightDir, j).up(height)).getBlock() != SCContent.reinforcedObsidian)
			{
				height = 0;
				break;
			}
		}

		if(height <= 21 && height >= 3)
			return height;
		else
		{
			bottomLeft = null;
			width = 0;
			height = 0;
			return 0;
		}
	}

	protected boolean isPartOfUnfinishedPortal(BlockState pos)
	{
		Block block = pos.getBlock();

		return pos.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
	}

	public boolean isValid()
	{
		return bottomLeft != null && width >= 2 && width <= 21 && height >= 3 && height <= 21;
	}

	public void placePortalBlocks()
	{
		for(int i = 0; i < width; ++i)
		{
			BlockPos pos = bottomLeft.offset(rightDir, i);

			for(int j = 0; j < this.height; ++j)
			{
				world.setBlockState(pos.up(j), Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis), 18);
			}
		}
	}

	private boolean doesCountExceedSize()
	{
		return portalBlockCount >= width * height;
	}

	public boolean isFinishedPortal()
	{
		return isValid() && doesCountExceedSize();
	}

	public int getPortalBlockCount()
	{
		return portalBlockCount;
	}
}