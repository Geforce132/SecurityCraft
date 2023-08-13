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
 * Vanilla code adapted to allow reinforced obsidian Borrowed from BlockPortal$Size
 */
public class PortalSize {
	private final World world;
	private final EnumFacing.Axis axis;
	private final EnumFacing rightDir;
	private final EnumFacing leftDir;
	private int portalBlockCount;
	private BlockPos bottomLeft;
	private int height;
	private int width;

	public PortalSize(World world, BlockPos pos, EnumFacing.Axis axis) {
		this.world = world;
		this.axis = axis;

		if (axis == EnumFacing.Axis.X) {
			leftDir = EnumFacing.EAST;
			rightDir = EnumFacing.WEST;
		}
		else {
			leftDir = EnumFacing.NORTH;
			rightDir = EnumFacing.SOUTH;
		}

		for (BlockPos blockPos = pos; pos.getY() > blockPos.getY() - 21 && pos.getY() > 0 && isEmptyBlock(world.getBlockState(pos.down()).getBlock()); pos = pos.down()) {}

		int distanceToEdge = getDistanceUntilEdge(pos, leftDir) - 1;

		if (distanceToEdge >= 0) {
			bottomLeft = pos.offset(leftDir, distanceToEdge);
			width = getDistanceUntilEdge(bottomLeft, rightDir);

			if (width < 2 || width > 21) {
				bottomLeft = null;
				width = 0;
			}
		}

		if (bottomLeft != null)
			height = calculatePortalHeight();
	}

	protected int getDistanceUntilEdge(BlockPos pos, EnumFacing facing) {
		int distance;

		for (distance = 0; distance < 22; ++distance) {
			BlockPos blockPos = pos.offset(facing, distance);

			if (!isEmptyBlock(world.getBlockState(blockPos).getBlock()) || world.getBlockState(blockPos.down()).getBlock() != SCContent.reinforcedObsidian)
				break;
		}

		Block block = world.getBlockState(pos.offset(facing, distance)).getBlock();
		return block == SCContent.reinforcedObsidian ? distance : 0;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	protected int calculatePortalHeight() {
		outerLoop:
		for (height = 0; height < 21; ++height) {
			for (int i = 0; i < width; ++i) {
				BlockPos pos = bottomLeft.offset(rightDir, i).up(height);
				Block block = world.getBlockState(pos).getBlock();

				if (!isEmptyBlock(block))
					break outerLoop;

				if (block == Blocks.PORTAL)
					++portalBlockCount;

				if (i == 0) {
					block = world.getBlockState(pos.offset(leftDir)).getBlock();

					if (block != SCContent.reinforcedObsidian)
						break outerLoop;
				}
				else if (i == width - 1) {
					block = world.getBlockState(pos.offset(rightDir)).getBlock();

					if (block != SCContent.reinforcedObsidian)
						break outerLoop;
				}
			}
		}

		for (int j = 0; j < width; ++j) {
			if (world.getBlockState(bottomLeft.offset(rightDir, j).up(height)).getBlock() != SCContent.reinforcedObsidian) {
				height = 0;
				break;
			}
		}

		if (height <= 21 && height >= 3)
			return height;
		else {
			bottomLeft = null;
			width = 0;
			height = 0;
			return 0;
		}
	}

	protected boolean isEmptyBlock(Block block) {
		return block.getMaterial(block.getDefaultState()) == Material.AIR || block == Blocks.FIRE || block == Blocks.PORTAL;
	}

	public boolean isValid() {
		return bottomLeft != null && width >= 2 && width <= 21 && height >= 3 && height <= 21;
	}

	public void placePortalBlocks() {
		for (int i = 0; i < width; ++i) {
			BlockPos pos = bottomLeft.offset(rightDir, i);

			for (int j = 0; j < height; ++j) {
				world.setBlockState(pos.up(j), Blocks.PORTAL.getDefaultState().withProperty(BlockPortal.AXIS, axis), 2 | 16);
			}
		}
	}

	public int getPortalBlockCount() {
		return portalBlockCount;
	}
}