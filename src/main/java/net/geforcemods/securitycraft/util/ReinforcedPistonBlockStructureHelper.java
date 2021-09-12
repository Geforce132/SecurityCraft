package net.geforcemods.securitycraft.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedPistonBlockStructureHelper { //this class doesn't extend PistonBlockStructureHelper because the content of that whole class is private

	private final World world;
	private final BlockPos pistonPos;
	private final boolean extending;
	private final BlockPos blockToMove;
	private final Direction moveDirection;
	private final List<BlockPos> toMove = Lists.newArrayList();
	private final List<BlockPos> toDestroy = Lists.newArrayList();
	private final Direction facing;

	public ReinforcedPistonBlockStructureHelper(World world, BlockPos pos, Direction pistonFacing, boolean extending) {
		this.world = world;
		this.pistonPos = pos;
		this.facing = pistonFacing;
		this.extending = extending;

		if (extending) {
			moveDirection = pistonFacing;
			blockToMove = pos.offset(pistonFacing);
		} else {
			moveDirection = pistonFacing.getOpposite();
			blockToMove = pos.offset(pistonFacing, 2);
		}
	}

	public boolean canMove() {
		BlockState state = world.getBlockState(blockToMove);

		toMove.clear();
		toDestroy.clear();

		if (!ReinforcedPistonBlock.canPush(state, world, pistonPos, blockToMove, moveDirection, false, facing)) {
			if (extending && state.getPushReaction() == PushReaction.DESTROY) {
				toDestroy.add(blockToMove);
				return true;
			} else {
				return false;
			}
		} else if (!addBlockLine(blockToMove, moveDirection)) {
			return false;
		} else {
			for(int i = 0; i < toMove.size(); ++i) {
				BlockPos pos = toMove.get(i);

				if (world.getBlockState(pos).isStickyBlock() && !addBranchingBlocks(pos)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean addBlockLine(BlockPos origin, Direction facing) {
		BlockState state = world.getBlockState(origin);

		if (world.isAirBlock(origin)) {
			return true;
		} else if (!ReinforcedPistonBlock.canPush(state, world, pistonPos, origin, moveDirection, false, facing)) {
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
				BlockState oldState;

				while(state.isStickyBlock()) {
					BlockPos blockpos = origin.offset(moveDirection.getOpposite(), i);

					oldState = state;
					state = world.getBlockState(blockpos);

					if (state.isAir(world, blockpos) || !oldState.canStickTo(state) || !ReinforcedPistonBlock.canPush(state, world, pistonPos, blockpos, moveDirection, false, moveDirection.getOpposite()) || blockpos.equals(pistonPos)) {
						break;
					}

					++i;

					if (i + toMove.size() > 12) {
						return false;
					}
				}

				int l = 0;

				for(int i1 = i - 1; i1 >= 0; --i1) {
					toMove.add(origin.offset(moveDirection.getOpposite(), i1));
					++l;
				}

				int j1 = 1;

				while(true) {
					BlockPos offsetPos = origin.offset(moveDirection, j1);
					int j = toMove.indexOf(offsetPos);

					if (j > -1) {
						reorderListAtCollision(l, j);

						for(int k = 0; k <= j + l; ++k) {
							BlockPos posToPush = toMove.get(k);
							if (world.getBlockState(posToPush).isStickyBlock() && !addBranchingBlocks(posToPush)) {
								return false;
							}
						}

						return true;
					}

					state = world.getBlockState(offsetPos);

					if (state.isAir(world, offsetPos)) {
						return true;
					}

					if (!ReinforcedPistonBlock.canPush(state, world, pistonPos, offsetPos, moveDirection, true, moveDirection) || offsetPos.equals(pistonPos)) {
						return false;
					}

					if (state.getPushReaction() == PushReaction.DESTROY) {
						toDestroy.add(offsetPos);
						return true;
					}

					if (toMove.size() >= 12) {
						return false;
					}

					toMove.add(offsetPos);
					++l;
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
		BlockState state = world.getBlockState(fromPos);

		for(Direction direction : Direction.values()) {
			if (direction.getAxis() != moveDirection.getAxis()) {
				BlockPos offsetPos = fromPos.offset(direction);
				BlockState offsetState = world.getBlockState(offsetPos);

				if (offsetState.canStickTo(state) && !addBlockLine(offsetPos, direction)) {
					return false;
				}
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
