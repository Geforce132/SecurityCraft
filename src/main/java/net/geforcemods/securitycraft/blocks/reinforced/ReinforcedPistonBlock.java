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
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.geforcemods.securitycraft.blockentities.ValidationOwnableBlockEntity;
import net.geforcemods.securitycraft.blocks.ElectrifiedIronFenceBlock;
import net.geforcemods.securitycraft.blocks.ElectrifiedIronFenceGateBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.ReinforcedPistonBlockStructureHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MovingPistonBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

public class ReinforcedPistonBlock extends PistonBlock implements IReinforcedBlock {
	public ReinforcedPistonBlock(boolean sticky, AbstractBlock.Properties properties) {
		super(sticky, properties);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public void onPlace(BlockState state, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!oldState.is(state.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) instanceof ValidationOwnableBlockEntity)
			checkIfExtend(level, pos, state);
	}

	@Override
	public void checkIfExtend(World level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		boolean hasSignal = shouldBeExtended(level, pos, direction);
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof OwnableBlockEntity && !((OwnableBlockEntity) te).getOwner().isValidated())
			return;

		if (hasSignal && !state.getValue(EXTENDED)) {
			if (new ReinforcedPistonBlockStructureHelper(level, pos, direction, true).canMove())
				level.blockEvent(pos, this, 0, direction.get3DDataValue());
		}
		else if (!hasSignal && state.getValue(EXTENDED)) {
			BlockPos offsetPos = pos.relative(direction, 2);
			BlockState offsetState = level.getBlockState(offsetPos);
			int i = 1;

			if (offsetState.is(SCContent.REINFORCED_MOVING_PISTON.get()) && offsetState.getValue(FACING) == direction) {
				TileEntity offsetTe = level.getBlockEntity(offsetPos);

				if (offsetTe instanceof ReinforcedPistonBlockEntity) {
					ReinforcedPistonBlockEntity pistonTileEntity = (ReinforcedPistonBlockEntity) offsetTe;

					if (pistonTileEntity.isExtending() && (pistonTileEntity.getProgress(0.0F) < 0.5F || level.getGameTime() == pistonTileEntity.getLastTicked() || ((ServerWorld) level).isHandlingTick()))
						i = 2;
				}
			}

			level.blockEvent(pos, this, i, direction.get3DDataValue());
		}
	}

	private boolean shouldBeExtended(World level, BlockPos pos, Direction direction) { // copied because shouldBeExtended() in PistonBlock is private
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
	public boolean triggerEvent(BlockState state, World level, BlockPos pos, int id, int param) {
		Direction direction = state.getValue(FACING);

		if (!level.isClientSide) {
			boolean isPowered = this.shouldBeExtended(level, pos, direction);

			if (isPowered && (id == 1 || id == 2)) {
				level.setBlock(pos, state.setValue(EXTENDED, true), 2);
				return false;
			}

			if (!isPowered && id == 0)
				return false;
		}

		if (id == 0) {
			if (ForgeEventFactory.onPistonMovePre(level, pos, direction, true))
				return false;

			if (!doMove(level, pos, direction, true))
				return false;

			level.setBlock(pos, state.setValue(EXTENDED, true), 67);
			level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.25F + 0.6F);
		}
		else if (id == 1 || id == 2) {
			if (ForgeEventFactory.onPistonMovePre(level, pos, direction, false))
				return false;

			TileEntity pistonBe = level.getBlockEntity(pos.relative(direction));

			if (pistonBe instanceof ReinforcedPistonBlockEntity)
				((ReinforcedPistonBlockEntity) pistonBe).finalTick();

			TileEntity be = level.getBlockEntity(pos);
			BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(MovingPistonBlock.FACING, direction).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);

			level.setBlock(pos, movingPiston, 20);
			level.setBlockEntity(pos, ReinforcedMovingPistonBlock.createTilePiston(this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(param & 7)), be != null ? be.getUpdateTag() : null, direction, false, true));
			level.blockUpdated(pos, movingPiston.getBlock());
			movingPiston.updateNeighbourShapes(level, pos, 2);

			if (isSticky) {
				BlockPos offsetPos = pos.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
				BlockState offsetState = level.getBlockState(offsetPos);
				boolean flag = false;

				if (offsetState.is(SCContent.REINFORCED_MOVING_PISTON.get())) {
					TileEntity offsetBe = level.getBlockEntity(offsetPos);

					if (offsetBe instanceof ReinforcedPistonBlockEntity) {
						ReinforcedPistonBlockEntity offsetPistonBe = (ReinforcedPistonBlockEntity) offsetBe;

						if (offsetPistonBe.getFacing() == direction && offsetPistonBe.isExtending()) {
							offsetPistonBe.finalTick();
							flag = true;
						}
					}
				}

				if (!flag) {
					if (id != 1 || offsetState.isAir() || !canPush(offsetState, level, pos, offsetPos, direction.getOpposite(), false, direction) || offsetState.getPistonPushReaction() != PushReaction.NORMAL && !offsetState.is(SCContent.REINFORCED_PISTON.get()) && !offsetState.is(SCContent.REINFORCED_STICKY_PISTON.get()))
						level.removeBlock(pos.relative(direction), false);
					else
						doMove(level, pos, direction, false);
				}
			}
			else
				level.removeBlock(pos.relative(direction), false);

			level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.15F + 0.6F);
		}

		ForgeEventFactory.onPistonMovePost(level, pos, direction, (id == 0));
		return true;
	}

	public static boolean canPush(BlockState state, World level, BlockPos pistonPos, BlockPos pos, Direction facing, boolean destroyBlocks, Direction direction) {
		if (pos.getY() >= 0 && pos.getY() < level.getMaxBuildHeight() && level.getWorldBorder().isWithinBounds(pos)) {
			if (state.isAir())
				return true;
			else if (!state.is(Blocks.OBSIDIAN) && !state.is(Blocks.CRYING_OBSIDIAN) && !state.is(Blocks.RESPAWN_ANCHOR) && !state.is(SCContent.REINFORCED_OBSIDIAN.get()) && !state.is(SCContent.REINFORCED_CRYING_OBSIDIAN.get())) {
				if ((facing == Direction.DOWN && pos.getY() == 0) || (facing == Direction.UP && pos.getY() == level.getMaxBuildHeight() - 1))
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

					return !state.hasTileEntity() || isPushableSCBlock;
				}
			}
		}

		return false;
	}

	private boolean doMove(World level, BlockPos pos, Direction facing, boolean extending) {
		BlockPos frontPos = pos.relative(facing);
		TileEntity pistonBe = level.getBlockEntity(pos);

		if (!extending && level.getBlockState(frontPos).is(SCContent.REINFORCED_PISTON_HEAD.get()))
			level.setBlock(frontPos, Blocks.AIR.defaultBlockState(), 20);

		ReinforcedPistonBlockStructureHelper structureResolver = new ReinforcedPistonBlockStructureHelper(level, pos, facing, extending);

		if (!structureResolver.canMove())
			return false;
		else {
			Map<BlockPos, BlockState> stateToPosMap = Maps.newHashMap();
			List<BlockPos> blocksToMove = structureResolver.getBlocksToMove();
			List<BlockState> statesToMove = Lists.newArrayList();

			for (int i = 0; i < blocksToMove.size(); ++i) {
				BlockPos posToMove = blocksToMove.get(i);
				BlockState stateToMove = level.getBlockState(posToMove);

				statesToMove.add(stateToMove);
				stateToPosMap.put(posToMove, stateToMove);
			}

			List<BlockPos> blocksToDestroy = structureResolver.getBlocksToDestroy();
			BlockState[] updatedBlocks = new BlockState[blocksToMove.size() + blocksToDestroy.size()];
			Direction direction = extending ? facing : facing.getOpposite();
			int j = 0;

			for (int k = blocksToDestroy.size() - 1; k >= 0; --k) {
				BlockPos posToDestroy = blocksToDestroy.get(k);
				BlockState stateToDestroy = level.getBlockState(posToDestroy);
				TileEntity beToDestroy = stateToDestroy.hasTileEntity() ? level.getBlockEntity(posToDestroy) : null;

				dropResources(stateToDestroy, level, posToDestroy, beToDestroy);
				level.setBlock(posToDestroy, Blocks.AIR.defaultBlockState(), 18);
				updatedBlocks[j++] = stateToDestroy;
			}

			for (int l = blocksToMove.size() - 1; l >= 0; --l) {
				BlockPos posToMove = blocksToMove.get(l);
				BlockState stateToMove = level.getBlockState(posToMove);
				TileEntity beToMove = level.getBlockEntity(posToMove);
				CompoundNBT tag = null;

				posToMove = posToMove.relative(direction);

				if (beToMove != null) {
					tag = new CompoundNBT();
					beToMove.setPosition(posToMove);
					beToMove.save(tag);
				}

				stateToPosMap.remove(posToMove);
				level.setBlock(posToMove, SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(FACING, facing), 68);
				level.setBlockEntity(posToMove, ReinforcedMovingPistonBlock.createTilePiston(statesToMove.get(l), tag, facing, extending, false));
				updatedBlocks[j++] = stateToMove;
			}

			if (extending) {
				PistonType type = isSticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState pistonHead = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(FACING, facing).setValue(PistonHeadBlock.TYPE, type);
				BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(MovingPistonBlock.FACING, facing).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
				OwnableBlockEntity headBe = new OwnableBlockEntity();

				if (pistonBe instanceof OwnableBlockEntity) //synchronize owner to the piston head
					headBe.setOwner(((OwnableBlockEntity) pistonBe).getOwner().getUUID(), ((OwnableBlockEntity) pistonBe).getOwner().getName());

				stateToPosMap.remove(frontPos);
				level.setBlock(frontPos, movingPiston, 68);
				level.setBlockEntity(frontPos, ReinforcedMovingPistonBlock.createTilePiston(pistonHead, headBe.getUpdateTag(), facing, true, true));
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
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new ValidationOwnableBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON;
	}

	@Override
	public BlockState convertToVanilla(World level, BlockPos pos, BlockState reinforcedState) {
		return IReinforcedBlock.super.convertToVanilla(level, pos, reinforcedState).setValue(EXTENDED, false);
	}

	@Override
	public BlockState convertToReinforced(World level, BlockPos pos, BlockState vanillaState) {
		return IReinforcedBlock.super.convertToReinforced(level, pos, vanillaState).setValue(EXTENDED, false);
	}

	private static boolean isSameOwner(BlockPos blockPos, BlockPos pistonPos, World level) {
		TileEntity pistonBe = level.getBlockEntity(pistonPos);
		IOwnable blockBe = (IOwnable) level.getBlockEntity(blockPos);

		if (pistonBe instanceof IOwnable)
			return blockBe.getOwner().owns(((IOwnable) pistonBe));

		return false;
	}
}
