package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedPistonTileEntity;
import net.geforcemods.securitycraft.tileentity.ValidationOwnableTileEntity;
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
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));

		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof OwnableTileEntity) {
			Owner owner = ((OwnableTileEntity)te).getOwner();

			if (!owner.isValidated()) {
				if (owner.isOwner(player)) {
					owner.setValidated(true);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), new TranslationTextComponent("messages.securitycraft:ownable.validate"), TextFormatting.GREEN);
					return ActionResultType.SUCCESS;
				}

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), new TranslationTextComponent("messages.securitycraft:ownable.ownerNotValidated"), TextFormatting.RED);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public void checkForMove(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		boolean hasSignal = shouldBeExtended(world, pos, direction);
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof OwnableTileEntity && !((OwnableTileEntity)te).getOwner().isValidated()) {
			return;
		}

		if (hasSignal && !state.get(EXTENDED)) {
			if ((new ReinforcedPistonBlockStructureHelper(world, pos, direction, true)).canMove()) {
				world.addBlockEvent(pos, this, 0, direction.getIndex());
			}
		} else if (!hasSignal && state.get(EXTENDED)) {
			BlockPos offsetPos = pos.offset(direction, 2);
			BlockState offsetState = world.getBlockState(offsetPos);
			int i = 1;

			if (offsetState.matchesBlock(SCContent.REINFORCED_MOVING_PISTON.get()) && offsetState.get(FACING) == direction) {
				TileEntity tileentity = world.getTileEntity(offsetPos);

				if (tileentity instanceof ReinforcedPistonTileEntity) {
					ReinforcedPistonTileEntity pistontileentity = (ReinforcedPistonTileEntity)tileentity;

					if (pistontileentity.isExtending() && (pistontileentity.getProgress(0.0F) < 0.5F || world.getGameTime() == pistontileentity.getLastTicked() || ((ServerWorld)world).isInsideTick())) {
						i = 2;
					}
				}
			}

			world.addBlockEvent(pos, this, i, direction.getIndex());
		}
	}

	private boolean shouldBeExtended(World world, BlockPos pos, Direction direction) { // copied because shouldBeExtended() in PistonBlock is private
		for(Direction dir : Direction.values()) {
			if (dir != direction && world.isSidePowered(pos.offset(dir), dir)) {
				return true;
			}
		}

		if (world.isSidePowered(pos, Direction.DOWN)) {
			return true;
		} else {
			BlockPos posAbove = pos.up();

			for(Direction dir : Direction.values()) {
				if (dir != Direction.DOWN && world.isSidePowered(posAbove.offset(dir), dir)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
		Direction direction = state.get(FACING);

		if (!world.isRemote) {
			boolean isPowered = this.shouldBeExtended(world, pos, direction);

			if (isPowered && (id == 1 || id == 2)) {
				world.setBlockState(pos, state.with(EXTENDED, true), 2);
				return false;
			}

			if (!isPowered && id == 0) {
				return false;
			}
		}

		if (id == 0) {
			if (ForgeEventFactory.onPistonMovePre(world, pos, direction, true))
				return false;

			if (!doMove(world, pos, direction, true)) {
				return false;
			}

			world.setBlockState(pos, state.with(EXTENDED, true), 67);
			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
		} else if (id == 1 || id == 2) {
			if (ForgeEventFactory.onPistonMovePre(world, pos, direction, false))
				return false;

			TileEntity pistonTE = world.getTileEntity(pos.offset(direction));

			if (pistonTE instanceof ReinforcedPistonTileEntity) {
				((ReinforcedPistonTileEntity)pistonTE).clearPistonTileEntity();
			}

			TileEntity te = world.getTileEntity(pos);
			BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().getDefaultState().with(MovingPistonBlock.FACING, direction).with(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);

			world.setBlockState(pos, movingPiston, 20);
			world.setTileEntity(pos, ReinforcedMovingPistonBlock.createTilePiston(this.getDefaultState().with(FACING, Direction.byIndex(param & 7)), te != null ? te.getUpdateTag() : null, direction, false, true));
			world.updateBlock(pos, movingPiston.getBlock());
			movingPiston.updateNeighbours(world, pos, 2);

			if (isSticky) {
				BlockPos offsetPos = pos.add(direction.getXOffset() * 2, direction.getYOffset() * 2, direction.getZOffset() * 2);
				BlockState offsetState = world.getBlockState(offsetPos);
				boolean flag = false;

				if (offsetState.matchesBlock(SCContent.REINFORCED_MOVING_PISTON.get())) {
					TileEntity offsetTe = world.getTileEntity(offsetPos);

					if (offsetTe instanceof ReinforcedPistonTileEntity) {
						ReinforcedPistonTileEntity pistonTe = (ReinforcedPistonTileEntity)offsetTe;

						if (pistonTe.getFacing() == direction && pistonTe.isExtending()) {
							pistonTe.clearPistonTileEntity();
							flag = true;
						}
					}
				}

				if (!flag) {
					if (id != 1 || offsetState.isAir() || !canPush(offsetState, world, pos, offsetPos, direction.getOpposite(), false, direction) || offsetState.getPushReaction() != PushReaction.NORMAL && !offsetState.matchesBlock(SCContent.REINFORCED_PISTON.get()) && !offsetState.matchesBlock(SCContent.REINFORCED_STICKY_PISTON.get())) {
						world.removeBlock(pos.offset(direction), false);
					} else {
						doMove(world, pos, direction, false);
					}
				}
			} else {
				world.removeBlock(pos.offset(direction), false);
			}

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		}

		ForgeEventFactory.onPistonMovePost(world, pos, direction, (id == 0));
		return true;
	}

	public static boolean canPush(BlockState state, World world, BlockPos pistonPos, BlockPos pos, Direction facing, boolean destroyBlocks, Direction direction) {
		if (pos.getY() >= 0 && pos.getY() < world.getHeight() && world.getWorldBorder().contains(pos)) {
			if (state.isAir()) {
				return true;
			} else if (!state.matchesBlock(Blocks.OBSIDIAN) && !state.matchesBlock(Blocks.CRYING_OBSIDIAN) && !state.matchesBlock(Blocks.RESPAWN_ANCHOR) && !state.matchesBlock(SCContent.REINFORCED_OBSIDIAN.get()) && !state.matchesBlock(SCContent.REINFORCED_CRYING_OBSIDIAN.get())) {
				if ((facing == Direction.DOWN && pos.getY() == 0) || (facing == Direction.UP && pos.getY() == world.getHeight() - 1)) {
					return false;
				}
				else {
					if (!state.matchesBlock(Blocks.PISTON) && !state.matchesBlock(Blocks.STICKY_PISTON) && !state.matchesBlock(SCContent.REINFORCED_PISTON.get()) && !state.matchesBlock(SCContent.REINFORCED_STICKY_PISTON.get())) {
						if (state.getBlock() instanceof IReinforcedBlock) {
							if (!isSameOwner(pos, pistonPos, world)) {
								return false;
							}
						}
						else if (state.getBlockHardness(world, pos) == -1.0F) {
							return false;
						}

						switch(state.getPushReaction()) {
							case BLOCK:
								return false;
							case DESTROY:
								return destroyBlocks;
							case PUSH_ONLY:
								return facing == direction;
							default: break;
						}
					} else if (state.get(EXTENDED)) {
						return false;
					}

					return !state.hasTileEntity() || state.getBlock() instanceof IReinforcedBlock;
				}
			}
		}

		return false;
	}

	private boolean doMove(World world, BlockPos pos, Direction facing, boolean extending) {
		BlockPos frontPos = pos.offset(facing);
		TileEntity pistonTe = world.getTileEntity(pos);

		if (!extending && world.getBlockState(frontPos).matchesBlock(SCContent.REINFORCED_PISTON_HEAD.get())) {
			world.setBlockState(frontPos, Blocks.AIR.getDefaultState(), 20);
		}

		ReinforcedPistonBlockStructureHelper structureHelper = new ReinforcedPistonBlockStructureHelper(world, pos, facing, extending);

		if (!structureHelper.canMove()) {
			return false;
		} else {
			Map<BlockPos, BlockState> stateToPosMap = Maps.newHashMap();
			List<BlockPos> blocksToMove = structureHelper.getBlocksToMove();
			List<BlockState> statesToMove = Lists.newArrayList();

			for(int i = 0; i < blocksToMove.size(); ++i) {
				BlockPos posToMove = blocksToMove.get(i);
				BlockState stateToMove = world.getBlockState(posToMove);

				statesToMove.add(stateToMove);
				stateToPosMap.put(posToMove, stateToMove);
			}

			List<BlockPos> blocksToDestroy = structureHelper.getBlocksToDestroy();
			BlockState[] updatedBlocks = new BlockState[blocksToMove.size() + blocksToDestroy.size()];
			Direction direction = extending ? facing : facing.getOpposite();
			int j = 0;

			for(int k = blocksToDestroy.size() - 1; k >= 0; --k) {
				BlockPos posToDestroy = blocksToDestroy.get(k);
				BlockState stateToDestroy = world.getBlockState(posToDestroy);
				TileEntity teToDestroy = stateToDestroy.hasTileEntity() ? world.getTileEntity(posToDestroy) : null;

				spawnDrops(stateToDestroy, world, posToDestroy, teToDestroy);
				world.setBlockState(posToDestroy, Blocks.AIR.getDefaultState(), 18);
				updatedBlocks[j++] = stateToDestroy;
			}

			for(int l = blocksToMove.size() - 1; l >= 0; --l) {
				BlockPos posToMove = blocksToMove.get(l);
				BlockState stateToMove = world.getBlockState(posToMove);
				TileEntity teToMove = world.getTileEntity(posToMove);
				CompoundNBT tag = null;

				if (teToMove != null){
					tag = new CompoundNBT();
					teToMove.setPos(posToMove.offset(direction));
					teToMove.write(tag);
				}

				posToMove = posToMove.offset(direction);
				stateToPosMap.remove(posToMove);
				world.setBlockState(posToMove, SCContent.REINFORCED_MOVING_PISTON.get().getDefaultState().with(FACING, facing), 68);
				world.setTileEntity(posToMove, ReinforcedMovingPistonBlock.createTilePiston(statesToMove.get(l), tag, facing, extending, false));
				updatedBlocks[j++] = stateToMove;
			}

			if (extending) {
				PistonType type = isSticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState pistonHead = SCContent.REINFORCED_PISTON_HEAD.get().getDefaultState().with(PistonHeadBlock.FACING, facing).with(PistonHeadBlock.TYPE, type);
				BlockState movingPiston = SCContent.REINFORCED_MOVING_PISTON.get().getDefaultState().with(MovingPistonBlock.FACING, facing).with(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
				OwnableTileEntity headTe = new OwnableTileEntity();

				if (pistonTe instanceof OwnableTileEntity) { //synchronize owner to the piston head
					headTe.setOwner(((OwnableTileEntity)pistonTe).getOwner().getUUID(), ((OwnableTileEntity)pistonTe).getOwner().getName());
				}

				stateToPosMap.remove(frontPos);
				world.setBlockState(frontPos, movingPiston, 68);
				world.setTileEntity(frontPos, ReinforcedMovingPistonBlock.createTilePiston(pistonHead, headTe.getUpdateTag(), facing, true, true));
			}

			BlockState air = Blocks.AIR.getDefaultState();

			for(BlockPos position : stateToPosMap.keySet()) {
				world.setBlockState(position, air, 82);
			}

			for(Entry<BlockPos, BlockState> entry : stateToPosMap.entrySet()) {
				BlockPos posToUpdate = entry.getKey();
				BlockState stateToUpdate = entry.getValue();

				stateToUpdate.updateDiagonalNeighbors(world, posToUpdate, 2);
				air.updateNeighbours(world, posToUpdate, 2);
				air.updateDiagonalNeighbors(world, posToUpdate, 2);
			}

			j = 0;

			for(int i1 = blocksToDestroy.size() - 1; i1 >= 0; --i1) {
				BlockState updatedState = updatedBlocks[j++];
				BlockPos posToDestroy = blocksToDestroy.get(i1);

				updatedState.updateDiagonalNeighbors(world, posToDestroy, 2);
				world.notifyNeighborsOfStateChange(posToDestroy, updatedState.getBlock());
			}

			for(int j1 = blocksToMove.size() - 1; j1 >= 0; --j1) {
				world.notifyNeighborsOfStateChange(blocksToMove.get(j1), updatedBlocks[j++].getBlock());
			}

			if (extending) {
				world.notifyNeighborsOfStateChange(frontPos, SCContent.REINFORCED_PISTON_HEAD.get());
			}

			return true;
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ValidationOwnableTileEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return getDefaultState().with(FACING, vanillaState.get(FACING)).with(EXTENDED, vanillaState.get(EXTENDED));
	}

	private static boolean isSameOwner(BlockPos blockPos, BlockPos pistonPos, World world) {
		TileEntity pistonTE = world.getTileEntity(pistonPos);
		IOwnable blockTE = (IOwnable)world.getTileEntity(blockPos);

		if (pistonTE instanceof IOwnable){
			return blockTE.getOwner().owns(((IOwnable)pistonTE));
		}

		return false;
	}
}
