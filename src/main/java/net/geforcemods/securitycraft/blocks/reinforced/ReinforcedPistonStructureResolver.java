package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ReinforcedPistonStructureResolver { //this class doesn't extend PistonStructureResolver because the content of that whole class is private

	private final Level level;
	private final BlockPos pistonPos;
	private final boolean extending;
	private final BlockPos startPos;
	private final Direction pushDirection;
	private final List<BlockPos> toPush = Lists.newArrayList();
	private final List<BlockPos> toDestroy = Lists.newArrayList();
	private final Direction pistonDirection;

	public ReinforcedPistonStructureResolver(Level level, BlockPos pos, Direction pistonFacing, boolean extending) {
		this.level = level;
		pistonPos = pos;
		pistonDirection = pistonFacing;
		this.extending = extending;

		if (extending) {
			pushDirection = pistonFacing;
			startPos = pos.relative(pistonFacing);
		} else {
			pushDirection = pistonFacing.getOpposite();
			startPos = pos.relative(pistonFacing, 2);
		}

	}

	public boolean resolve() {
		BlockState state = level.getBlockState(startPos);

		toPush.clear();
		toDestroy.clear();

		if (!ReinforcedPistonBaseBlock.isPushable(state, level, pistonPos, startPos, pushDirection, false, pistonDirection)) {
			if (extending && state.getPistonPushReaction() == PushReaction.DESTROY) {
				toDestroy.add(startPos);
				return true;
			} else {
				return false;
			}
		} else if (!addBlockLine(startPos, pushDirection)) {
			return false;
		} else {
			for(int i = 0; i < toPush.size(); ++i) {
				BlockPos pos = toPush.get(i);

				if (level.getBlockState(pos).isStickyBlock() && !addBranchingBlocks(pos)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean addBlockLine(BlockPos originPos, Direction facing) {
		BlockState state = level.getBlockState(originPos);

		if (level.isEmptyBlock(originPos)) {
			return true;
		} else if (!ReinforcedPistonBaseBlock.isPushable(state, level, pistonPos, originPos, pushDirection, false, facing)) {
			return true;
		} else if (originPos.equals(pistonPos)) {
			return true;
		} else if (toPush.contains(originPos)) {
			return true;
		} else {
			int i = 1;
			if (i + toPush.size() > 12) {
				return false;
			} else {
				BlockState oldState;

				while(state.isStickyBlock()) {
					BlockPos offsetPos = originPos.relative(pushDirection.getOpposite(), i);

					oldState = state;
					state = level.getBlockState(offsetPos);

					if (state.isAir() || !oldState.canStickTo(state) || !ReinforcedPistonBaseBlock.isPushable(state, level, pistonPos, offsetPos, pushDirection, false, pushDirection.getOpposite()) || offsetPos.equals(pistonPos)) {
						break;
					}

					++i;

					if (i + toPush.size() > 12) {
						return false;
					}
				}

				int l = 0;

				for(int i1 = i - 1; i1 >= 0; --i1) {
					toPush.add(originPos.relative(pushDirection.getOpposite(), i1));
					++l;
				}

				int j1 = 1;

				while(true) {
					BlockPos offsetPos = originPos.relative(pushDirection, j1);

					int j = toPush.indexOf(offsetPos);

					if (j > -1) {
						this.reorderListAtCollision(l, j);

						for(int k = 0; k <= j + l; ++k) {
							BlockPos posToPush = toPush.get(k);

							if (level.getBlockState(posToPush).isStickyBlock() && !addBranchingBlocks(posToPush)) {
								return false;
							}
						}

						return true;
					}

					state = level.getBlockState(offsetPos);

					if (state.isAir()) {
						return true;
					}

					if (!ReinforcedPistonBaseBlock.isPushable(state, level, pistonPos, offsetPos, pushDirection, true, pushDirection) || offsetPos.equals(pistonPos)) {
						return false;
					}

					if (state.getPistonPushReaction() == PushReaction.DESTROY) {
						toDestroy.add(offsetPos);
						return true;
					}

					if (toPush.size() >= 12) {
						return false;
					}

					toPush.add(offsetPos);
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

		list.addAll(toPush.subList(0, index));
		list1.addAll(toPush.subList(toPush.size() - offsets, toPush.size()));
		list2.addAll(toPush.subList(index, toPush.size() - offsets));
		toPush.clear();
		toPush.addAll(list);
		toPush.addAll(list1);
		toPush.addAll(list2);
	}

	private boolean addBranchingBlocks(BlockPos fromPos) {
		BlockState state = level.getBlockState(fromPos);

		for(Direction direction : Direction.values()) {
			if (direction.getAxis() != pushDirection.getAxis()) {
				BlockPos offsetPos = fromPos.relative(direction);
				BlockState offsetState = level.getBlockState(offsetPos);

				if (offsetState.canStickTo(state) && !addBlockLine(offsetPos, direction)) {
					return false;
				}
			}
		}

		return true;
	}

	public Direction getPushDirection() {
		return pushDirection;
	}

	public List<BlockPos> getToPush() {
		return this.toPush;
	}

	public List<BlockPos> getToDestroy() {
		return this.toDestroy;
	}
}
