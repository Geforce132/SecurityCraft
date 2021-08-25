package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

public class ReinforcedPistonBaseBlock extends PistonBaseBlock implements IReinforcedBlock, EntityBlock {

	public ReinforcedPistonBaseBlock(boolean sticky, Properties properties) {
		super(sticky, properties);
	}

	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(worldIn, pos, player));

		super.setPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public void checkIfExtend(Level world, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		boolean hasSignal = this.getNeighborSignal(world, pos, direction);

		if (hasSignal && !state.getValue(EXTENDED)) {
			if ((new ReinforcedPistonStructureResolver(world, pos, direction, true)).resolve()) {
				world.blockEvent(pos, this, 0, direction.get3DDataValue());
			}
		} else if (!hasSignal && state.getValue(EXTENDED)) {
			BlockPos blockpos = pos.relative(direction, 2);
			BlockState blockstate = world.getBlockState(blockpos);
			int i = 1;

			if (blockstate.is(SCContent.REINFORCED_MOVING_PISTON.get()) && blockstate.getValue(FACING) == direction) {
				BlockEntity te = world.getBlockEntity(blockpos);

				if (te instanceof ReinforcedPistonMovingBlockEntity pistonTileEntity) {
					if (pistonTileEntity.isExtending() && (pistonTileEntity.getProgress(0.0F) < 0.5F || world.getGameTime() == pistonTileEntity.getLastTicked() || ((ServerLevel)world).isHandlingTick())) {
						i = 2;
					}
				}
			}

			world.blockEvent(pos, this, i, direction.get3DDataValue());
		}

	}

	private boolean getNeighborSignal(Level worldIn, BlockPos pos, Direction facing) { // copied because shouldBeExtended() in PistonBlock is private
		for(Direction direction : Direction.values()) {
			if (direction != facing && worldIn.hasSignal(pos.relative(direction), direction)) {
				return true;
			}
		}

		if (worldIn.hasSignal(pos, Direction.DOWN)) {
			return true;
		} else {
			BlockPos blockpos = pos.above();

			for(Direction direction1 : Direction.values()) {
				if (direction1 != Direction.DOWN && worldIn.hasSignal(blockpos.relative(direction1), direction1)) {
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Called on server when {@link net.minecraft.world.level.Level#blockEvent} is called. If server returns true, then
	 * also called on the client. On the Server, this may perform additional changes to the world, like pistons replacing
	 * the block with an extended base. On the client, the update may involve replacing tile entities or effects such as
	 * sounds or particles
	 */
	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param) {
		Direction direction = state.getValue(FACING);
		if (!world.isClientSide) {
			boolean isPowered = this.getNeighborSignal(world, pos, direction);
			if (isPowered && (id == 1 || id == 2)) {
				world.setBlock(pos, state.setValue(EXTENDED, true), 2);
				return false;
			}

			if (!isPowered && id == 0) {
				return false;
			}
		}

		if (id == 0) {
			if (ForgeEventFactory.onPistonMovePre(world, pos, direction, true)) return false;
			if (!this.moveBlocks(world, pos, direction, true)) {
				return false;
			}

			world.setBlock(pos, state.setValue(EXTENDED, true), 67);
			world.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
			world.gameEvent(GameEvent.PISTON_EXTEND, pos);
		} else if (id == 1 || id == 2) {
			if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(world, pos, direction, false)) return false;
			BlockEntity pistonTE = world.getBlockEntity(pos.relative(direction));
			if (pistonTE instanceof ReinforcedPistonMovingBlockEntity) {
				((ReinforcedPistonMovingBlockEntity)pistonTE).finalTick();
			}

			BlockEntity te = world.getBlockEntity(pos);
			BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(FACING, direction).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
			
			world.setBlock(pos, movingPiston, 20);
			world.setBlockEntity(ReinforcedMovingPistonBlock.newMovingBlockEntity(pos, movingPiston, this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(param & 7)), te != null ? te.getUpdateTag() : null, direction, false, true));
			world.blockUpdated(pos, movingPiston.getBlock());
			movingPiston.updateNeighbourShapes(world, pos, 2);
			
			if (this.isSticky) {
				BlockPos blockpos = pos.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
				BlockState blockstate1 = world.getBlockState(blockpos);
				boolean flag1 = false;
				
				if (blockstate1.is(SCContent.REINFORCED_MOVING_PISTON.get())) {
					BlockEntity tileentity = world.getBlockEntity(blockpos);
					
					if (tileentity instanceof ReinforcedPistonMovingBlockEntity pistontileentity) {
						if (pistontileentity.getFacing() == direction && pistontileentity.isExtending()) {
							pistontileentity.finalTick();
							flag1 = true;
						}
					}
				}

				if (!flag1) {
					if (id != 1 || blockstate1.isAir() || !isPushable(blockstate1, world, pos, blockpos, direction.getOpposite(), false, direction) || blockstate1.getPistonPushReaction() != PushReaction.NORMAL && !blockstate1.is(SCContent.REINFORCED_PISTON.get()) && !blockstate1.is(SCContent.REINFORCED_STICKY_PISTON.get())) {
						world.removeBlock(pos.relative(direction), false);
					} else {
						this.moveBlocks(world, pos, direction, false);
					}
				}
			} else {
				world.removeBlock(pos.relative(direction), false);
			}

			world.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
		}

		net.minecraftforge.event.ForgeEventFactory.onPistonMovePost(world, pos, direction, (id == 0));
		return true;
	}

	public static boolean isPushable(BlockState state, Level world, BlockPos pistonPos, BlockPos pos, Direction facing, boolean destroyBlocks, Direction direction) {
		if (pos.getY() >= 0 && pos.getY() <= world.getHeight() - 1 && world.getWorldBorder().isWithinBounds(pos)) {
			if (state.isAir()) {
				return true;
			} else if (!state.is(Blocks.OBSIDIAN) && !state.is(Blocks.CRYING_OBSIDIAN) && !state.is(Blocks.RESPAWN_ANCHOR) && !state.is(SCContent.REINFORCED_OBSIDIAN.get()) && !state.is(SCContent.REINFORCED_CRYING_OBSIDIAN.get())) {
				if ((facing == Direction.DOWN && pos.getY() == world.getMinBuildHeight()) || (facing == Direction.UP && pos.getY() == world.getHeight() - 1)) {
					return false;
				}
				else {
					if (!state.is(Blocks.PISTON) && !state.is(Blocks.STICKY_PISTON) && !state.is(SCContent.REINFORCED_PISTON.get()) && !state.is(SCContent.REINFORCED_STICKY_PISTON.get())) {
						if (state.getBlock() instanceof IReinforcedBlock) {
							return isSameOwner(pos, pistonPos, world);
						}
						else if (state.getDestroySpeed(world, pos) == -1.0F) {
							return false;
						}

						switch(state.getPistonPushReaction()) {
							case BLOCK:
								return false;
							case DESTROY:
								return destroyBlocks;
							case PUSH_ONLY:
								return facing == direction;
						}
					} else if (state.getValue(EXTENDED)) {
						return false;
					}

					return !state.hasBlockEntity() || state.getBlock() instanceof IReinforcedBlock;
				}
			}
		}

		return false;
	}

	private boolean moveBlocks(Level world, BlockPos pos, Direction facing, boolean extending) {
		BlockPos frontPos = pos.relative(facing);
		BlockEntity pistonTe = world.getBlockEntity(pos);

		if (!extending && world.getBlockState(frontPos).is(SCContent.REINFORCED_PISTON_HEAD.get())) {
			world.setBlock(frontPos, Blocks.AIR.defaultBlockState(), 20);
		}

		ReinforcedPistonStructureResolver structureHelper = new ReinforcedPistonStructureResolver(world, pos, facing, extending);
		if (!structureHelper.resolve()) {
			return false;
		} else {
			Map<BlockPos, BlockState> stateToPosMap = Maps.newHashMap();
			List<BlockPos> blocksToMove = structureHelper.getToPush();
			List<BlockState> statesToMove = Lists.newArrayList();

			for(int i = 0; i < blocksToMove.size(); ++i) {
				BlockPos blockpos1 = blocksToMove.get(i);
				BlockState blockstate = world.getBlockState(blockpos1);
				statesToMove.add(blockstate);
				stateToPosMap.put(blockpos1, blockstate);
			}


			List<BlockPos> blocksToDestroy = structureHelper.getToDestroy();
			BlockState[] updatedBlocks = new BlockState[blocksToMove.size() + blocksToDestroy.size()];
			Direction direction = extending ? facing : facing.getOpposite();
			int j = 0;

			for(int k = blocksToDestroy.size() - 1; k >= 0; --k) {
				BlockPos blockpos2 = blocksToDestroy.get(k);
				BlockState blockstate1 = world.getBlockState(blockpos2);
				BlockEntity tileentity = blockstate1.hasBlockEntity() ? world.getBlockEntity(blockpos2) : null;

				dropResources(blockstate1, world, blockpos2, tileentity);
				world.setBlock(blockpos2, Blocks.AIR.defaultBlockState(), 18);

				if (!blockstate1.is(BlockTags.FIRE)) {
					world.addDestroyBlockEffect(blockpos2, blockstate1);
				}

				updatedBlocks[j++] = blockstate1;
			}

			for(int l = blocksToMove.size() - 1; l >= 0; --l) {
				BlockPos posToMove = blocksToMove.get(l);
				BlockState stateToMove = world.getBlockState(posToMove);
				BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(FACING, direction);
				BlockEntity te = world.getBlockEntity(posToMove);
				CompoundTag tag = null;

				posToMove = posToMove.relative(direction);

				if (te != null){
					tag = new CompoundTag();
					te.save(tag);
					tag.putInt("x", posToMove.getX());
					tag.putInt("y", posToMove.getY());
					tag.putInt("z", posToMove.getZ());
				}

				stateToPosMap.remove(posToMove);
				world.setBlock(posToMove, SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(FACING, facing), 68);
				world.setBlockEntity(ReinforcedMovingPistonBlock.newMovingBlockEntity(posToMove, movingPiston, statesToMove.get(l), tag, facing, extending, false));
				updatedBlocks[j++] = stateToMove;
			}

			if (extending) {
				PistonType type = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState pistonHead = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.FACING, facing).setValue(PistonHeadBlock.TYPE, type);
				BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(MovingPistonBlock.FACING, facing).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
				OwnableBlockEntity headTe = new OwnableBlockEntity(frontPos, movingPiston);

				if (pistonTe instanceof OwnableBlockEntity) { //synchronize owner to the piston head
					headTe.setOwner(((OwnableBlockEntity)pistonTe).getOwner().getUUID(), ((OwnableBlockEntity)pistonTe).getOwner().getName());
				}

				stateToPosMap.remove(frontPos);
				world.setBlock(frontPos, movingPiston, 68);
				world.setBlockEntity(ReinforcedMovingPistonBlock.newMovingBlockEntity(frontPos, movingPiston, pistonHead, headTe.getUpdateTag(), facing, true, true));
			}

			BlockState air = Blocks.AIR.defaultBlockState();

			for(BlockPos blockpos4 : stateToPosMap.keySet()) {
				world.setBlock(blockpos4, air, 82);
			}

			for(Entry<BlockPos, BlockState> entry : stateToPosMap.entrySet()) {
				BlockPos blockpos5 = entry.getKey();
				BlockState blockstate2 = entry.getValue();
				blockstate2.updateIndirectNeighbourShapes(world, blockpos5, 2);
				air.updateNeighbourShapes(world, blockpos5, 2);
				air.updateIndirectNeighbourShapes(world, blockpos5, 2);
			}

			j = 0;

			for(int i1 = blocksToDestroy.size() - 1; i1 >= 0; --i1) {
				BlockState blockstate7 = updatedBlocks[j++];
				BlockPos blockpos6 = blocksToDestroy.get(i1);
				blockstate7.updateIndirectNeighbourShapes(world, blockpos6, 2);
				world.updateNeighborsAt(blockpos6, blockstate7.getBlock());
			}

			for(int j1 = blocksToMove.size() - 1; j1 >= 0; --j1) {
				world.updateNeighborsAt(blocksToMove.get(j1), updatedBlocks[j++].getBlock());
			}

			if (extending) {
				world.updateNeighborsAt(frontPos, SCContent.REINFORCED_PISTON_HEAD.get());
			}

			return true;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(pos, state);
	}

	@Override
	public Block getVanillaBlock() {
		return isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return defaultBlockState().setValue(FACING, vanillaState.getValue(FACING)).setValue(EXTENDED, vanillaState.getValue(EXTENDED));
	}

	private static boolean isSameOwner(BlockPos blockPos, BlockPos pistonPos, Level world) {
		BlockEntity pistonTE = world.getBlockEntity(pistonPos);
		IOwnable blockTE = (IOwnable)world.getBlockEntity(blockPos);

		if (pistonTE instanceof IOwnable){
			return blockTE.getOwner().owns(((IOwnable)pistonTE));
		}

		return false;
	}
}
