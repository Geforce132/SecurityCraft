package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ReinforcedPistonStructureResolver { //this class doesn't extend PistonStructureResolver because the content of that whole class is private

	private final Level world;
	private final BlockPos pistonPos;
	private final boolean extending;
	private final BlockPos startPos;
	private final Direction pushDirection;
	/** All block positions to be moved by the piston */
	private final List<BlockPos> toPush = Lists.newArrayList();
	/** All blocks to be destroyed by the piston */
	private final List<BlockPos> toDestroy = Lists.newArrayList();
	private final Direction pistonDirection;

	public ReinforcedPistonStructureResolver(Level world, BlockPos pos, Direction pistonFacing, boolean extending) {
		this.world = world;
		this.pistonPos = pos;
		this.pistonDirection = pistonFacing;
		this.extending = extending;
		if (extending) {
			this.pushDirection = pistonFacing;
			this.startPos = pos.relative(pistonFacing);
		} else {
			this.pushDirection = pistonFacing.getOpposite();
			this.startPos = pos.relative(pistonFacing, 2);
		}

	}

	public boolean resolve() {
		this.toPush.clear();
		this.toDestroy.clear();
		BlockState blockstate = this.world.getBlockState(this.startPos);

		if (!ReinforcedPistonBaseBlock.isPushable(blockstate, this.world, this.pistonPos, this.startPos, this.pushDirection, false, this.pistonDirection)) {
			if (this.extending && blockstate.getPistonPushReaction() == PushReaction.DESTROY) {
				this.toDestroy.add(this.startPos);
				return true;
			} else {
				return false;
			}
		} else if (!this.addBlockLine(this.startPos, this.pushDirection)) {
			return false;
		} else {
			for(int i = 0; i < this.toPush.size(); ++i) {
				BlockPos blockpos = this.toPush.get(i);
				if (this.world.getBlockState(blockpos).isStickyBlock() && !this.addBranchingBlocks(blockpos)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean addBlockLine(BlockPos originPos, Direction facing) {
		BlockState blockstate = this.world.getBlockState(originPos);
		if (world.isEmptyBlock(originPos)) {
			return true;
		} else if (!ReinforcedPistonBaseBlock.isPushable(blockstate, this.world, this.pistonPos, originPos, this.pushDirection, false, facing)) {
			return true;
		} else if (originPos.equals(this.pistonPos)) {
			return true;
		} else if (this.toPush.contains(originPos)) {
			return true;
		} else {
			int i = 1;
			if (i + this.toPush.size() > 12) {
				return false;
			} else {
				BlockState oldState;
				while(blockstate.isStickyBlock()) {
					BlockPos blockpos = originPos.relative(this.pushDirection.getOpposite(), i);
					oldState = blockstate;
					blockstate = this.world.getBlockState(blockpos);
					if (blockstate.isAir() || !oldState.canStickTo(blockstate) || !ReinforcedPistonBaseBlock.isPushable(blockstate, this.world, this.pistonPos, blockpos, this.pushDirection, false, this.pushDirection.getOpposite()) || blockpos.equals(this.pistonPos)) {
						break;
					}

					++i;
					if (i + this.toPush.size() > 12) {
						return false;
					}
				}

				int l = 0;

				for(int i1 = i - 1; i1 >= 0; --i1) {
					this.toPush.add(originPos.relative(this.pushDirection.getOpposite(), i1));
					++l;
				}

				int j1 = 1;

				while(true) {
					BlockPos blockpos1 = originPos.relative(this.pushDirection, j1);
					int j = this.toPush.indexOf(blockpos1);
					if (j > -1) {
						this.reorderListAtCollision(l, j);

						for(int k = 0; k <= j + l; ++k) {
							BlockPos blockpos2 = this.toPush.get(k);
							if (this.world.getBlockState(blockpos2).isStickyBlock() && !this.addBranchingBlocks(blockpos2)) {
								return false;
							}
						}

						return true;
					}

					blockstate = this.world.getBlockState(blockpos1);
					if (blockstate.isAir()) {
						return true;
					}

					if (!ReinforcedPistonBaseBlock.isPushable(blockstate, this.world, this.pistonPos, blockpos1, this.pushDirection, true, this.pushDirection) || blockpos1.equals(this.pistonPos)) {
						return false;
					}

					if (blockstate.getPistonPushReaction() == PushReaction.DESTROY) {
						this.toDestroy.add(blockpos1);
						return true;
					}

					if (this.toPush.size() >= 12) {
						return false;
					}

					this.toPush.add(blockpos1);
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

		list.addAll(this.toPush.subList(0, index));
		list1.addAll(this.toPush.subList(this.toPush.size() - offsets, this.toPush.size()));
		list2.addAll(this.toPush.subList(index, this.toPush.size() - offsets));
		this.toPush.clear();
		this.toPush.addAll(list);
		this.toPush.addAll(list1);
		this.toPush.addAll(list2);
	}

	private boolean addBranchingBlocks(BlockPos fromPos) {
		BlockState blockstate = this.world.getBlockState(fromPos);

		for(Direction direction : Direction.values()) {
			if (direction.getAxis() != this.pushDirection.getAxis()) {
				BlockPos blockpos = fromPos.relative(direction);
				BlockState blockstate1 = this.world.getBlockState(blockpos);
				if (blockstate1.canStickTo(blockstate) && !this.addBlockLine(blockpos, direction)) {
					return false;
				}
			}
		}

		return true;
	}

	public Direction getPushDirection() {
		return this.pushDirection;
	}

	/**
	 * @return all block positions to be moved by the piston
	 */
	public List<BlockPos> getToPush() {
		return this.toPush;
	}

	/**
	 * @return all block positions to be destroyed by the piston
	 */
	public List<BlockPos> getToDestroy() {
		return this.toDestroy;
	}
}
