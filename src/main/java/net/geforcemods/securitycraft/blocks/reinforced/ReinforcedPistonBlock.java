package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedPistonTileEntity;
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

public class ReinforcedPistonBlock extends PistonBlock implements IReinforcedBlock {

	public ReinforcedPistonBlock(boolean sticky, Properties properties) {
		super(sticky, properties);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(worldIn, pos, (PlayerEntity)placer));

		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public void checkForMove(World worldIn, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		boolean flag = this.shouldBeExtended(worldIn, pos, direction);
		if (flag && !state.get(EXTENDED)) {
			if ((new ReinforcedPistonBlockStructureHelper(worldIn, pos, direction, true)).canMove()) {
				worldIn.addBlockEvent(pos, this, 0, direction.getIndex());
			}
		} else if (!flag && state.get(EXTENDED)) {
			BlockPos blockpos = pos.offset(direction, 2);
			BlockState blockstate = worldIn.getBlockState(blockpos);
			int i = 1;
			if (blockstate.matchesBlock(SCContent.REINFORCED_MOVING_PISTON.get()) && blockstate.get(FACING) == direction) {
				TileEntity tileentity = worldIn.getTileEntity(blockpos);
				if (tileentity instanceof ReinforcedPistonTileEntity) {
					ReinforcedPistonTileEntity pistontileentity = (ReinforcedPistonTileEntity)tileentity;
					if (pistontileentity.isExtending() && (pistontileentity.getProgress(0.0F) < 0.5F || worldIn.getGameTime() == pistontileentity.getLastTicked() || ((ServerWorld)worldIn).isInsideTick())) {
						i = 2;
					}
				}
			}

			worldIn.addBlockEvent(pos, this, i, direction.getIndex());
		}

	}

	private boolean shouldBeExtended(World worldIn, BlockPos pos, Direction facing) { // copied because shouldBeExtended() in PistonBlock is private
		for(Direction direction : Direction.values()) {
			if (direction != facing && worldIn.isSidePowered(pos.offset(direction), direction)) {
				return true;
			}
		}

		if (worldIn.isSidePowered(pos, Direction.DOWN)) {
			return true;
		} else {
			BlockPos blockpos = pos.up();

			for(Direction direction1 : Direction.values()) {
				if (direction1 != Direction.DOWN && worldIn.isSidePowered(blockpos.offset(direction1), direction1)) {
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
	 * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
	 * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
	 * @deprecated call via {@link BlockState#receiveBlockEvent(World,BlockPos,int,int)} whenever possible.
	 * Implementing/overriding is fine.
	 */
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
			if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(world, pos, direction, true)) return false;
			if (!this.doMove(world, pos, direction, true)) {
				return false;
			}

			world.setBlockState(pos, state.with(EXTENDED, true), 67);
			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
		} else if (id == 1 || id == 2) {
			if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(world, pos, direction, false)) return false;
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
			if (this.isSticky) {
				BlockPos blockpos = pos.add(direction.getXOffset() * 2, direction.getYOffset() * 2, direction.getZOffset() * 2);
				BlockState blockstate1 = world.getBlockState(blockpos);
				boolean flag1 = false;
				if (blockstate1.matchesBlock(SCContent.REINFORCED_MOVING_PISTON.get())) {
					TileEntity tileentity = world.getTileEntity(blockpos);
					if (tileentity instanceof ReinforcedPistonTileEntity) {
						ReinforcedPistonTileEntity pistontileentity = (ReinforcedPistonTileEntity)tileentity;
						if (pistontileentity.getFacing() == direction && pistontileentity.isExtending()) {
							pistontileentity.clearPistonTileEntity();
							flag1 = true;
						}
					}
				}

				if (!flag1) {
					if (id != 1 || blockstate1.isAir() || !ReinforcedPistonBlock.canPush(blockstate1, world, pos, blockpos, direction.getOpposite(), false, direction) || blockstate1.getPushReaction() != PushReaction.NORMAL && !blockstate1.matchesBlock(SCContent.REINFORCED_PISTON.get()) && !blockstate1.matchesBlock(SCContent.REINFORCED_STICKY_PISTON.get())) {
						world.removeBlock(pos.offset(direction), false);
					} else {
						this.doMove(world, pos, direction, false);
					}
				}
			} else {
				world.removeBlock(pos.offset(direction), false);
			}

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		}

		net.minecraftforge.event.ForgeEventFactory.onPistonMovePost(world, pos, direction, (id == 0));
		return true;
	}

	public static boolean canPush(BlockState state, World world, BlockPos pistonPos, BlockPos pos, Direction facing, boolean destroyBlocks, Direction direction) {
		if (pos.getY() >= 0 && pos.getY() <= world.getHeight() - 1 && world.getWorldBorder().contains(pos)) {
			if (state.isAir()) {
				return true;
			} else if (!state.matchesBlock(Blocks.OBSIDIAN) && !state.matchesBlock(Blocks.CRYING_OBSIDIAN) && !state.matchesBlock(Blocks.RESPAWN_ANCHOR) && !state.matchesBlock(SCContent.REINFORCED_OBSIDIAN.get()) && !state.matchesBlock(SCContent.REINFORCED_CRYING_OBSIDIAN.get())) {
				if ((facing == Direction.DOWN && pos.getY() == 0) || (facing == Direction.UP && pos.getY() == world.getHeight() - 1)) {
					return false;
				}
				else {
					if (!state.matchesBlock(Blocks.PISTON) && !state.matchesBlock(Blocks.STICKY_PISTON) && !state.matchesBlock(SCContent.REINFORCED_PISTON.get()) && !state.matchesBlock(SCContent.REINFORCED_STICKY_PISTON.get())) {
						if (state.getBlock() instanceof IReinforcedBlock) {
							return isSameOwner(pos, pistonPos, world);
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
			List<BlockState> list1 = Lists.newArrayList();

			for(int i = 0; i < blocksToMove.size(); ++i) {
				BlockPos blockpos1 = blocksToMove.get(i);
				BlockState blockstate = world.getBlockState(blockpos1);
				list1.add(blockstate);
				stateToPosMap.put(blockpos1, blockstate);
			}

			List<BlockPos> blocksToDestroy = structureHelper.getBlocksToDestroy();
			BlockState[] updatedBlocks = new BlockState[blocksToMove.size() + blocksToDestroy.size()];
			Direction direction = extending ? facing : facing.getOpposite();
			int j = 0;

			for(int k = blocksToDestroy.size() - 1; k >= 0; --k) {
				BlockPos blockpos2 = blocksToDestroy.get(k);
				BlockState blockstate1 = world.getBlockState(blockpos2);
				TileEntity tileentity = blockstate1.hasTileEntity() ? world.getTileEntity(blockpos2) : null;
				spawnDrops(blockstate1, world, blockpos2, tileentity);
				world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 18);
				updatedBlocks[j++] = blockstate1;
			}

			for(int l = blocksToMove.size() - 1; l >= 0; --l) {
				BlockPos posToMove = blocksToMove.get(l);
				BlockState stateToMove = world.getBlockState(posToMove);
				TileEntity te = world.getTileEntity(posToMove);
				CompoundNBT tag = null;

				if (te != null){
					tag = new CompoundNBT();
					te.setPos(posToMove.offset(direction));
					te.write(tag);
				}

				posToMove = posToMove.offset(direction);
				stateToPosMap.remove(posToMove);
				world.setBlockState(posToMove, SCContent.REINFORCED_MOVING_PISTON.get().getDefaultState().with(FACING, facing), 68);
				world.setTileEntity(posToMove, ReinforcedMovingPistonBlock.createTilePiston(list1.get(l), tag, facing, extending, false));
				updatedBlocks[j++] = stateToMove;
			}

			if (extending) {
				PistonType type = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
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

			for(BlockPos blockpos4 : stateToPosMap.keySet()) {
				world.setBlockState(blockpos4, air, 82);
			}

			for(Entry<BlockPos, BlockState> entry : stateToPosMap.entrySet()) {
				BlockPos blockpos5 = entry.getKey();
				BlockState blockstate2 = entry.getValue();
				blockstate2.updateDiagonalNeighbors(world, blockpos5, 2);
				air.updateNeighbours(world, blockpos5, 2);
				air.updateDiagonalNeighbors(world, blockpos5, 2);
			}

			j = 0;

			for(int i1 = blocksToDestroy.size() - 1; i1 >= 0; --i1) {
				BlockState blockstate7 = updatedBlocks[j++];
				BlockPos blockpos6 = blocksToDestroy.get(i1);
				blockstate7.updateDiagonalNeighbors(world, blockpos6, 2);
				world.notifyNeighborsOfStateChange(blockpos6, blockstate7.getBlock());
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
		return new OwnableTileEntity();
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
