package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.geforcemods.securitycraft.blockentities.ValidationOwnableBlockEntity;
import net.geforcemods.securitycraft.blocks.ElectrifiedIronFenceBlock;
import net.geforcemods.securitycraft.blocks.ElectrifiedIronFenceGateBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.ReinforcedPistonStructureResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;

public class ReinforcedPistonBaseBlock extends PistonBaseBlock implements IReinforcedBlock, EntityBlock {
	public ReinforcedPistonBaseBlock(boolean sticky, BlockBehaviour.Properties properties) {
		super(sticky, properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!oldState.is(state.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) instanceof ValidationOwnableBlockEntity)
			checkIfExtend(level, pos, state);
	}

	@Override
	public void checkIfExtend(Level level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		boolean hasSignal = getNeighborSignal(level, pos, direction);

		if (level.getBlockEntity(pos) instanceof OwnableBlockEntity be && !be.getOwner().isValidated())
			return;

		if (hasSignal && !state.getValue(EXTENDED)) {
			if (new ReinforcedPistonStructureResolver(level, pos, direction, true).resolve())
				level.blockEvent(pos, this, 0, direction.get3DDataValue());
		}
		else if (!hasSignal && state.getValue(EXTENDED)) {
			BlockPos offsetPos = pos.relative(direction, 2);
			BlockState offsetState = level.getBlockState(offsetPos);
			int i = 1;

			if (offsetState.is(SCContent.REINFORCED_MOVING_PISTON.get()) && offsetState.getValue(FACING) == direction && level.getBlockEntity(offsetPos) instanceof ReinforcedPistonMovingBlockEntity pistonTileEntity) {
				if (pistonTileEntity.isExtending() && (pistonTileEntity.getProgress(0.0F) < 0.5F || level.getGameTime() == pistonTileEntity.getLastTicked() || ((ServerLevel) level).isHandlingTick()))
					i = 2;
			}

			level.blockEvent(pos, this, i, direction.get3DDataValue());
		}
	}

	private boolean getNeighborSignal(Level level, BlockPos pos, Direction direction) { // copied because getNeighborSignal() in PistonBaseBlock is private
		for (Direction dir : Direction.values()) {
			if (dir != direction && level.hasSignal(pos.relative(dir), dir))
				return true;
		}

		if (level.hasSignal(pos, Direction.DOWN))
			return true;
		else {
			BlockPos posAbove = pos.above();

			for (Direction dir : Direction.values()) {
				if (dir != Direction.DOWN && level.hasSignal(posAbove.relative(dir), dir))
					return true;
			}

			return false;
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		Direction direction = state.getValue(FACING);
		BlockState extendedState = state.setValue(EXTENDED, true);

		if (!level.isClientSide) {
			boolean isPowered = getNeighborSignal(level, pos, direction);

			if (isPowered && (id == 1 || id == 2)) {
				level.setBlock(pos, extendedState, 2);
				return false;
			}

			if (!isPowered && id == 0)
				return false;
		}

		if (id == 0) {
			if (EventHooks.onPistonMovePre(level, pos, direction, true))
				return false;

			if (!this.moveBlocks(level, pos, direction, true))
				return false;

			level.setBlock(pos, extendedState, 67);
			level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.25F + 0.6F);
			level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(extendedState));
		}
		else if (id == 1 || id == 2) {
			if (EventHooks.onPistonMovePre(level, pos, direction, false))
				return false;

			if (level.getBlockEntity(pos.relative(direction)) instanceof ReinforcedPistonMovingBlockEntity pistonBe)
				pistonBe.finalTick();

			BlockEntity be = level.getBlockEntity(pos);
			BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(FACING, direction).setValue(MovingPistonBlock.TYPE, isSticky ? PistonType.STICKY : PistonType.DEFAULT);

			level.setBlock(pos, movingPiston, 20);
			level.setBlockEntity(ReinforcedMovingPistonBlock.newMovingBlockEntity(pos, movingPiston, defaultBlockState().setValue(FACING, Direction.from3DDataValue(param & 7)), be != null ? be.getUpdateTag() : null, direction, false, true));
			level.blockUpdated(pos, movingPiston.getBlock());
			movingPiston.updateNeighbourShapes(level, pos, 2);

			if (isSticky) {
				BlockPos offsetPos = pos.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
				BlockState offsetState = level.getBlockState(offsetPos);
				boolean flag = false;

				if (offsetState.is(SCContent.REINFORCED_MOVING_PISTON.get()) && level.getBlockEntity(offsetPos) instanceof ReinforcedPistonMovingBlockEntity pistonBe && pistonBe.getFacing() == direction && pistonBe.isExtending()) {
					pistonBe.finalTick();
					flag = true;
				}

				if (!flag) {
					if (id != 1 || offsetState.isAir() || !isPushable(offsetState, level, pos, offsetPos, direction.getOpposite(), false, direction) || offsetState.getPistonPushReaction() != PushReaction.NORMAL && !offsetState.is(SCContent.REINFORCED_PISTON.get()) && !offsetState.is(SCContent.REINFORCED_STICKY_PISTON.get()))
						level.removeBlock(pos.relative(direction), false);
					else
						moveBlocks(level, pos, direction, false);
				}
			}
			else
				level.removeBlock(pos.relative(direction), false);

			level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.15F + 0.6F);
			level.gameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Context.of(movingPiston));
		}

		EventHooks.onPistonMovePost(level, pos, direction, id == 0);
		return true;
	}

	public static boolean isPushable(BlockState state, Level level, BlockPos pistonPos, BlockPos pos, Direction facing, boolean destroyBlocks, Direction direction) {
		if (pos.getY() >= level.getMinBuildHeight() && pos.getY() <= level.getMaxBuildHeight() - 1 && level.getWorldBorder().isWithinBounds(pos)) {
			if (state.isAir())
				return true;
			else if (!state.is(Blocks.OBSIDIAN) && !state.is(Blocks.CRYING_OBSIDIAN) && !state.is(Blocks.RESPAWN_ANCHOR) && !state.is(Blocks.REINFORCED_DEEPSLATE) && !state.is(SCContent.REINFORCED_OBSIDIAN.get()) && !state.is(SCContent.REINFORCED_CRYING_OBSIDIAN.get())) {
				if ((facing == Direction.DOWN && pos.getY() == level.getMinBuildHeight()) || (facing == Direction.UP && pos.getY() == level.getMaxBuildHeight() - 1))
					return false;
				else {
					boolean isPushableSCBlock = state.getBlock() instanceof IReinforcedBlock || state.getBlock() instanceof ElectrifiedIronFenceBlock || state.getBlock() instanceof ElectrifiedIronFenceGateBlock;

					if (!state.is(Blocks.PISTON) && !state.is(Blocks.STICKY_PISTON) && !state.is(SCContent.REINFORCED_PISTON.get()) && !state.is(SCContent.REINFORCED_STICKY_PISTON.get())) {
						if (isPushableSCBlock) {
							if (!isSameOwner(pos, pistonPos, level))
								return false;
						}
						else if (state.getDestroySpeed(level, pos) == -1.0F)
							return false;

						switch (state.getPistonPushReaction()) {
							case BLOCK:
								return false;
							case DESTROY:
								return destroyBlocks;
							case PUSH_ONLY:
								return facing == direction;
							default:
								break;
						}
					}
					else if (state.getValue(EXTENDED))
						return false;

					return !state.hasBlockEntity() || isPushableSCBlock;
				}
			}
		}

		return false;
	}

	private boolean moveBlocks(Level level, BlockPos pos, Direction facing, boolean extending) {
		BlockPos frontPos = pos.relative(facing);
		BlockEntity pistonBe = level.getBlockEntity(pos);

		if (!extending && level.getBlockState(frontPos).is(SCContent.REINFORCED_PISTON_HEAD.get()))
			level.setBlock(frontPos, Blocks.AIR.defaultBlockState(), 20);

		ReinforcedPistonStructureResolver structureResolver = new ReinforcedPistonStructureResolver(level, pos, facing, extending);

		if (!structureResolver.resolve())
			return false;
		else {
			Map<BlockPos, BlockState> stateToPosMap = Maps.newHashMap();
			List<BlockPos> blocksToMove = structureResolver.getToPush();
			List<BlockState> statesToMove = Lists.newArrayList();

			for (int i = 0; i < blocksToMove.size(); ++i) {
				BlockPos posToMove = blocksToMove.get(i);
				BlockState stateToMove = level.getBlockState(posToMove);

				statesToMove.add(stateToMove);
				stateToPosMap.put(posToMove, stateToMove);
			}

			List<BlockPos> blocksToDestroy = structureResolver.getToDestroy();
			BlockState[] updatedBlocks = new BlockState[blocksToMove.size() + blocksToDestroy.size()];
			Direction direction = extending ? facing : facing.getOpposite();
			int j = 0;

			for (int k = blocksToDestroy.size() - 1; k >= 0; --k) {
				BlockPos posToDestroy = blocksToDestroy.get(k);
				BlockState stateToDestroy = level.getBlockState(posToDestroy);
				BlockEntity beToDestroy = stateToDestroy.hasBlockEntity() ? level.getBlockEntity(posToDestroy) : null;

				dropResources(stateToDestroy, level, posToDestroy, beToDestroy);
				level.setBlock(posToDestroy, Blocks.AIR.defaultBlockState(), 18);
				level.gameEvent(GameEvent.BLOCK_DESTROY, posToDestroy, GameEvent.Context.of(stateToDestroy));

				if (!stateToDestroy.is(BlockTags.FIRE))
					level.addDestroyBlockEffect(posToDestroy, stateToDestroy);

				updatedBlocks[j++] = stateToDestroy;
			}

			for (int l = blocksToMove.size() - 1; l >= 0; --l) {
				BlockPos posToMove = blocksToMove.get(l);
				BlockState stateToMove = level.getBlockState(posToMove);
				BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(FACING, direction);
				BlockEntity beToMove = level.getBlockEntity(posToMove);
				CompoundTag tag = null;

				posToMove = posToMove.relative(direction);

				if (beToMove != null) {
					tag = beToMove.saveWithoutMetadata();
					tag.putInt("x", posToMove.getX());
					tag.putInt("y", posToMove.getY());
					tag.putInt("z", posToMove.getZ());
				}

				stateToPosMap.remove(posToMove);
				level.setBlock(posToMove, SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(FACING, facing), 68);
				level.setBlockEntity(ReinforcedMovingPistonBlock.newMovingBlockEntity(posToMove, movingPiston, statesToMove.get(l), tag, facing, extending, false));
				updatedBlocks[j++] = stateToMove;
			}

			if (extending) {
				PistonType type = isSticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState pistonHead = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(FACING, facing).setValue(PistonHeadBlock.TYPE, type);
				BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(MovingPistonBlock.FACING, facing).setValue(MovingPistonBlock.TYPE, isSticky ? PistonType.STICKY : PistonType.DEFAULT);
				OwnableBlockEntity headBe = new OwnableBlockEntity(frontPos, movingPiston);

				if (pistonBe instanceof OwnableBlockEntity ownable) //synchronize owner to the piston head
					headBe.setOwner(ownable.getOwner().getUUID(), ownable.getOwner().getName());

				stateToPosMap.remove(frontPos);
				level.setBlock(frontPos, movingPiston, 68);
				level.setBlockEntity(ReinforcedMovingPistonBlock.newMovingBlockEntity(frontPos, movingPiston, pistonHead, headBe.getUpdateTag(), facing, true, true));
			}

			BlockState air = Blocks.AIR.defaultBlockState();

			for (BlockPos position : stateToPosMap.keySet()) {
				level.setBlock(position, air, 82);
			}

			for (Entry<BlockPos, BlockState> entry : stateToPosMap.entrySet()) {
				BlockPos posToUpdate = entry.getKey();
				BlockState stateToUpdate = entry.getValue();

				stateToUpdate.updateIndirectNeighbourShapes(level, posToUpdate, 2);
				air.updateNeighbourShapes(level, posToUpdate, 2);
				air.updateIndirectNeighbourShapes(level, posToUpdate, 2);
			}

			j = 0;

			for (int i1 = blocksToDestroy.size() - 1; i1 >= 0; --i1) {
				BlockState updatedState = updatedBlocks[j++];
				BlockPos posToDestroy = blocksToDestroy.get(i1);

				updatedState.updateIndirectNeighbourShapes(level, posToDestroy, 2);
				level.updateNeighborsAt(posToDestroy, updatedState.getBlock());
			}

			for (int j1 = blocksToMove.size() - 1; j1 >= 0; --j1) {
				level.updateNeighborsAt(blocksToMove.get(j1), updatedBlocks[j++].getBlock());
			}

			if (extending)
				level.updateNeighborsAt(frontPos, SCContent.REINFORCED_PISTON_HEAD.get());

			return true;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ValidationOwnableBlockEntity(pos, state);
	}

	@Override
	public Block getVanillaBlock() {
		return isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON;
	}

	private static boolean isSameOwner(BlockPos blockPos, BlockPos pistonPos, Level level) {
		BlockEntity pistonBe = level.getBlockEntity(pistonPos);
		IOwnable blockBe = (IOwnable) level.getBlockEntity(blockPos);

		if (pistonBe instanceof IOwnable ownable)
			return blockBe.getOwner().owns(ownable);

		return false;
	}
}
