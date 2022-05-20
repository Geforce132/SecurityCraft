package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.geforcemods.securitycraft.blockentities.ValidationOwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.ReinforcedPistonBlockStructureHelper;
import net.geforcemods.securitycraft.util.Utils;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

public class ReinforcedPistonBlock extends PistonBlock implements IReinforcedBlock {
	public ReinforcedPistonBlock(boolean sticky, Properties properties) {
		super(sticky, properties);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity) placer));

		super.setPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getBlockEntity(pos);

		if (te instanceof OwnableBlockEntity) {
			Owner owner = ((OwnableBlockEntity) te).getOwner();

			if (!owner.isValidated()) {
				if (owner.isOwner(player)) {
					owner.setValidated(true);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), new TranslationTextComponent("messages.securitycraft:ownable.validate"), TextFormatting.GREEN);
					return ActionResultType.SUCCESS;
				}

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), new TranslationTextComponent("messages.securitycraft:ownable.ownerNotValidated"), TextFormatting.RED);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public void checkIfExtend(World world, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		boolean hasSignal = shouldBeExtended(world, pos, direction);
		TileEntity te = world.getBlockEntity(pos);

		if (te instanceof OwnableBlockEntity && !((OwnableBlockEntity) te).getOwner().isValidated())
			return;

		if (hasSignal && !state.getValue(EXTENDED)) {
			if ((new ReinforcedPistonBlockStructureHelper(world, pos, direction, true)).canMove())
				world.blockEvent(pos, this, 0, direction.get3DDataValue());
		}
		else if (!hasSignal && state.getValue(EXTENDED)) {
			BlockPos offsetPos = pos.relative(direction, 2);
			BlockState offsetState = world.getBlockState(offsetPos);
			int i = 1;

			if (offsetState.is(SCContent.REINFORCED_MOVING_PISTON.get()) && offsetState.getValue(FACING) == direction) {
				TileEntity tileentity = world.getBlockEntity(offsetPos);

				if (tileentity instanceof ReinforcedPistonBlockEntity) {
					ReinforcedPistonBlockEntity pistontileentity = (ReinforcedPistonBlockEntity) tileentity;

					if (pistontileentity.isExtending() && (pistontileentity.getProgress(0.0F) < 0.5F || world.getGameTime() == pistontileentity.getLastTicked() || ((ServerWorld) world).isHandlingTick()))
						i = 2;
				}
			}

			world.blockEvent(pos, this, i, direction.get3DDataValue());
		}
	}

	private boolean shouldBeExtended(World world, BlockPos pos, Direction direction) { // copied because shouldBeExtended() in PistonBlock is private
		for (Direction dir : Direction.values()) {
			if (dir != direction && world.hasSignal(pos.relative(dir), dir))
				return true;
		}

		if (world.hasSignal(pos, Direction.DOWN))
			return true;
		else {
			BlockPos posAbove = pos.above();

			for (Direction dir : Direction.values()) {
				if (dir != Direction.DOWN && world.hasSignal(posAbove.relative(dir), dir))
					return true;
			}

			return false;
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {
		Direction direction = state.getValue(FACING);

		if (!world.isClientSide) {
			boolean isPowered = this.shouldBeExtended(world, pos, direction);

			if (isPowered && (id == 1 || id == 2)) {
				world.setBlock(pos, state.setValue(EXTENDED, true), 2);
				return false;
			}

			if (!isPowered && id == 0)
				return false;
		}

		if (id == 0) {
			if (ForgeEventFactory.onPistonMovePre(world, pos, direction, true))
				return false;

			if (!doMove(world, pos, direction, true))
				return false;

			world.setBlock(pos, state.setValue(EXTENDED, true), 67);
			world.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
		}
		else if (id == 1 || id == 2) {
			if (ForgeEventFactory.onPistonMovePre(world, pos, direction, false))
				return false;

			TileEntity pistonTE = world.getBlockEntity(pos.relative(direction));

			if (pistonTE instanceof ReinforcedPistonBlockEntity)
				((ReinforcedPistonBlockEntity) pistonTE).clearPistonTileEntity();

			TileEntity te = world.getBlockEntity(pos);
			BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(MovingPistonBlock.FACING, direction).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);

			world.setBlock(pos, movingPiston, 20);
			world.setBlockEntity(pos, ReinforcedMovingPistonBlock.createTilePiston(this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(param & 7)), te != null ? te.getUpdateTag() : null, direction, false, true));
			world.blockUpdated(pos, movingPiston.getBlock());
			movingPiston.updateNeighbourShapes(world, pos, 2);

			if (isSticky) {
				BlockPos offsetPos = pos.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
				BlockState offsetState = world.getBlockState(offsetPos);
				boolean flag = false;

				if (offsetState.is(SCContent.REINFORCED_MOVING_PISTON.get())) {
					TileEntity offsetTe = world.getBlockEntity(offsetPos);

					if (offsetTe instanceof ReinforcedPistonBlockEntity) {
						ReinforcedPistonBlockEntity pistonTe = (ReinforcedPistonBlockEntity) offsetTe;

						if (pistonTe.getFacing() == direction && pistonTe.isExtending()) {
							pistonTe.clearPistonTileEntity();
							flag = true;
						}
					}
				}

				if (!flag) {
					if (id != 1 || offsetState.isAir() || !canPush(offsetState, world, pos, offsetPos, direction.getOpposite(), false, direction) || offsetState.getPistonPushReaction() != PushReaction.NORMAL && !offsetState.is(SCContent.REINFORCED_PISTON.get()) && !offsetState.is(SCContent.REINFORCED_STICKY_PISTON.get()))
						world.removeBlock(pos.relative(direction), false);
					else
						doMove(world, pos, direction, false);
				}
			}
			else
				world.removeBlock(pos.relative(direction), false);

			world.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
		}

		ForgeEventFactory.onPistonMovePost(world, pos, direction, (id == 0));
		return true;
	}

	public static boolean canPush(BlockState state, World world, BlockPos pistonPos, BlockPos pos, Direction facing, boolean destroyBlocks, Direction direction) {
		if (pos.getY() >= 0 && pos.getY() < world.getMaxBuildHeight() && world.getWorldBorder().isWithinBounds(pos)) {
			if (state.isAir())
				return true;
			else if (!state.is(Blocks.OBSIDIAN) && !state.is(Blocks.CRYING_OBSIDIAN) && !state.is(Blocks.RESPAWN_ANCHOR) && !state.is(SCContent.REINFORCED_OBSIDIAN.get()) && !state.is(SCContent.REINFORCED_CRYING_OBSIDIAN.get())) {
				if ((facing == Direction.DOWN && pos.getY() == 0) || (facing == Direction.UP && pos.getY() == world.getMaxBuildHeight() - 1))
					return false;
				else {
					if (!state.is(Blocks.PISTON) && !state.is(Blocks.STICKY_PISTON) && !state.is(SCContent.REINFORCED_PISTON.get()) && !state.is(SCContent.REINFORCED_STICKY_PISTON.get())) {
						if (state.getBlock() instanceof IReinforcedBlock) {
							if (!isSameOwner(pos, pistonPos, world))
								return false;
						}
						else if (state.getDestroySpeed(world, pos) == -1.0F)
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

					return !state.hasTileEntity() || state.getBlock() instanceof IReinforcedBlock;
				}
			}
		}

		return false;
	}

	private boolean doMove(World world, BlockPos pos, Direction facing, boolean extending) {
		BlockPos frontPos = pos.relative(facing);
		TileEntity pistonTe = world.getBlockEntity(pos);

		if (!extending && world.getBlockState(frontPos).is(SCContent.REINFORCED_PISTON_HEAD.get()))
			world.setBlock(frontPos, Blocks.AIR.defaultBlockState(), 20);

		ReinforcedPistonBlockStructureHelper structureHelper = new ReinforcedPistonBlockStructureHelper(world, pos, facing, extending);

		if (!structureHelper.canMove())
			return false;
		else {
			Map<BlockPos, BlockState> stateToPosMap = Maps.newHashMap();
			List<BlockPos> blocksToMove = structureHelper.getBlocksToMove();
			List<BlockState> statesToMove = Lists.newArrayList();

			for (int i = 0; i < blocksToMove.size(); ++i) {
				BlockPos posToMove = blocksToMove.get(i);
				BlockState stateToMove = world.getBlockState(posToMove);

				statesToMove.add(stateToMove);
				stateToPosMap.put(posToMove, stateToMove);
			}

			List<BlockPos> blocksToDestroy = structureHelper.getBlocksToDestroy();
			BlockState[] updatedBlocks = new BlockState[blocksToMove.size() + blocksToDestroy.size()];
			Direction direction = extending ? facing : facing.getOpposite();
			int j = 0;

			for (int k = blocksToDestroy.size() - 1; k >= 0; --k) {
				BlockPos posToDestroy = blocksToDestroy.get(k);
				BlockState stateToDestroy = world.getBlockState(posToDestroy);
				TileEntity teToDestroy = stateToDestroy.hasTileEntity() ? world.getBlockEntity(posToDestroy) : null;

				dropResources(stateToDestroy, world, posToDestroy, teToDestroy);
				world.setBlock(posToDestroy, Blocks.AIR.defaultBlockState(), 18);
				updatedBlocks[j++] = stateToDestroy;
			}

			for (int l = blocksToMove.size() - 1; l >= 0; --l) {
				BlockPos posToMove = blocksToMove.get(l);
				BlockState stateToMove = world.getBlockState(posToMove);
				TileEntity teToMove = world.getBlockEntity(posToMove);
				CompoundNBT tag = null;

				if (teToMove != null) {
					tag = new CompoundNBT();
					teToMove.setPosition(posToMove.relative(direction));
					teToMove.save(tag);
				}

				posToMove = posToMove.relative(direction);
				stateToPosMap.remove(posToMove);
				world.setBlock(posToMove, SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(FACING, facing), 68);
				world.setBlockEntity(posToMove, ReinforcedMovingPistonBlock.createTilePiston(statesToMove.get(l), tag, facing, extending, false));
				updatedBlocks[j++] = stateToMove;
			}

			if (extending) {
				PistonType type = isSticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState pistonHead = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.FACING, facing).setValue(PistonHeadBlock.TYPE, type);
				BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().defaultBlockState().setValue(MovingPistonBlock.FACING, facing).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
				OwnableBlockEntity headTe = new OwnableBlockEntity();

				if (pistonTe instanceof OwnableBlockEntity) //synchronize owner to the piston head
					headTe.setOwner(((OwnableBlockEntity) pistonTe).getOwner().getUUID(), ((OwnableBlockEntity) pistonTe).getOwner().getName());

				stateToPosMap.remove(frontPos);
				world.setBlock(frontPos, movingPiston, 68);
				world.setBlockEntity(frontPos, ReinforcedMovingPistonBlock.createTilePiston(pistonHead, headTe.getUpdateTag(), facing, true, true));
			}

			BlockState air = Blocks.AIR.defaultBlockState();

			for (BlockPos position : stateToPosMap.keySet()) {
				world.setBlock(position, air, 82);
			}

			for (Entry<BlockPos, BlockState> entry : stateToPosMap.entrySet()) {
				BlockPos posToUpdate = entry.getKey();
				BlockState stateToUpdate = entry.getValue();

				stateToUpdate.updateIndirectNeighbourShapes(world, posToUpdate, 2);
				air.updateNeighbourShapes(world, posToUpdate, 2);
				air.updateIndirectNeighbourShapes(world, posToUpdate, 2);
			}

			j = 0;

			for (int i1 = blocksToDestroy.size() - 1; i1 >= 0; --i1) {
				BlockState updatedState = updatedBlocks[j++];
				BlockPos posToDestroy = blocksToDestroy.get(i1);

				updatedState.updateIndirectNeighbourShapes(world, posToDestroy, 2);
				world.updateNeighborsAt(posToDestroy, updatedState.getBlock());
			}

			for (int j1 = blocksToMove.size() - 1; j1 >= 0; --j1) {
				world.updateNeighborsAt(blocksToMove.get(j1), updatedBlocks[j++].getBlock());
			}

			if (extending)
				world.updateNeighborsAt(frontPos, SCContent.REINFORCED_PISTON_HEAD.get());

			return true;
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ValidationOwnableBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON;
	}

	private static boolean isSameOwner(BlockPos blockPos, BlockPos pistonPos, World world) {
		TileEntity pistonTE = world.getBlockEntity(pistonPos);
		IOwnable blockTE = (IOwnable) world.getBlockEntity(blockPos);

		if (pistonTE instanceof IOwnable)
			return blockTE.getOwner().owns(((IOwnable) pistonTE));

		return false;
	}
}
