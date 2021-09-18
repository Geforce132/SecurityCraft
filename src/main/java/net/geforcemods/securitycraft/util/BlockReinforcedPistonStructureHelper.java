package net.geforcemods.securitycraft.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedPistonBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedPistonStructureHelper { //this class doesn't extend BlockPistonStructureHelper because the content of that whole class is private

	private final World world;
	private final BlockPos pistonPos;
	private final BlockPos blockToMove;
	private final EnumFacing moveDirection;
	private final List<BlockPos> toMove = Lists.newArrayList();
	private final List<BlockPos> toDestroy = Lists.newArrayList();

	public BlockReinforcedPistonStructureHelper(World world, BlockPos pos, EnumFacing pistonFacing, boolean extending) {
		this.world = world;
		this.pistonPos = pos;

		if (extending) {
			moveDirection = pistonFacing;
			blockToMove = pos.offset(pistonFacing);
		} else {
			moveDirection = pistonFacing.getOpposite();
			blockToMove = pos.offset(pistonFacing, 2);
		}
	}

	public boolean canMove() {
		IBlockState state = world.getBlockState(blockToMove);

		toMove.clear();
		toDestroy.clear();

		if (!BlockReinforcedPistonBase.canPush(state, world, pistonPos, blockToMove, moveDirection, false, moveDirection)) {
			if (state.getPushReaction() == EnumPushReaction.DESTROY) {
				toDestroy.add(blockToMove);
				return true;
			} else {
				return false;
			}
		} else if (!addBlockLine(blockToMove, moveDirection)) {
			return false;
		} else {
			for (int i = 0; i < toMove.size(); ++i) {
				BlockPos pos = toMove.get(i);

				if (world.getBlockState(pos).getBlock().isStickyBlock(world.getBlockState(pos)) && !addBranchingBlocks(pos)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean addBlockLine(BlockPos origin, EnumFacing facing) {
		IBlockState state = world.getBlockState(origin);
		Block block = state.getBlock();

		if (state.getBlock().isAir(state, world, origin)) {
			return true;
		} else if (!BlockReinforcedPistonBase.canPush(state, world, pistonPos, origin, moveDirection, false, facing)) {
			return true;
		} else if (origin.equals(pistonPos)) {
			return true;
		} else if (toMove.contains(origin)) {
			return true;
		} else {
			int i = 1;

			if (i + toMove.size() > 12) {
				return false;
			} else {
				while (block.isStickyBlock(state)) {
					BlockPos blockpos = origin.offset(moveDirection.getOpposite(), i);
					state = world.getBlockState(blockpos);
					block = state.getBlock();

					if (state.getBlock().isAir(state, world, blockpos) || !BlockReinforcedPistonBase.canPush(state, world, pistonPos, blockpos, moveDirection, false, moveDirection.getOpposite()) || blockpos.equals(pistonPos)) {
						break;
					}

					++i;

					if (i + toMove.size() > 12) {
						return false;
					}
				}

				int i1 = 0;

				for (int j = i - 1; j >= 0; --j) {
					toMove.add(origin.offset(moveDirection.getOpposite(), j));
					++i1;
				}

				int j1 = 1;

				while (true) {
					BlockPos offsetPos = origin.offset(moveDirection, j1);
					int k = toMove.indexOf(offsetPos);

					if (k > -1) {
						reorderListAtCollision(i1, k);

						for (int l = 0; l <= k + i1; ++l) {
							BlockPos posToPush = toMove.get(l);

							if (world.getBlockState(posToPush).getBlock().isStickyBlock(world.getBlockState(posToPush)) && !addBranchingBlocks(posToPush)) {
								return false;
							}
						}

						return true;
					}

					state = world.getBlockState(offsetPos);

					if (state.getBlock().isAir(state, world, offsetPos)) {
						return true;
					}

					if (!BlockReinforcedPistonBase.canPush(state, world, pistonPos, offsetPos, moveDirection, true, moveDirection) || offsetPos.equals(pistonPos)) {
						return false;
					}

					if (state.getPushReaction() == EnumPushReaction.DESTROY) {
						toDestroy.add(offsetPos);
						return true;
					}

					if (toMove.size() >= 12) {
						return false;
					}

					toMove.add(offsetPos);
					++i1;
					++j1;
				}
			}
		}
	}

	private void reorderListAtCollision(int offsets, int index) {
		List<BlockPos> list = Lists.newArrayList();
		List<BlockPos> list1 = Lists.newArrayList();
		List<BlockPos> list2 = Lists.newArrayList();

		list.addAll(toMove.subList(0, index));
		list1.addAll(toMove.subList(toMove.size() - offsets, toMove.size()));
		list2.addAll(toMove.subList(index, toMove.size() - offsets));
		toMove.clear();
		toMove.addAll(list);
		toMove.addAll(list1);
		toMove.addAll(list2);
	}

	private boolean addBranchingBlocks(BlockPos fromPos) {
		for (EnumFacing direction : EnumFacing.values()) {
			if (direction.getAxis() != moveDirection.getAxis() && !addBlockLine(fromPos.offset(direction), direction)) {
				return false;
			}
		}

		return true;
	}

	public List<BlockPos> getBlocksToMove() {
		return toMove;
	}

	public List<BlockPos> getBlocksToDestroy() {
		return toDestroy;
	}
}
