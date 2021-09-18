package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedPiston;
import net.geforcemods.securitycraft.tileentity.TileEntityValidationOwnable;
import net.geforcemods.securitycraft.util.BlockReinforcedPistonStructureHelper;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedPistonBase extends BlockPistonBase implements IReinforcedBlock, ITileEntityProvider {

	public BlockReinforcedPistonBase(boolean isSticky) {
		super(isSticky);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));

		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityOwnable) {
			Owner owner = ((TileEntityOwnable)te).getOwner();

			if (!owner.isValidated()) {
				if (owner.isOwner(player)) {
					owner.setValidated(true);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:ownable.validate"), TextFormatting.GREEN);
					return true;
				}

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:ownable.ownerNotValidated"), TextFormatting.RED);
				return true;
			}
		}

		return false;
	}


	@Override
	public void checkForMove(World world, BlockPos pos, IBlockState state) {
		EnumFacing facing = state.getValue(FACING);
		boolean hasSignal = shouldBeExtended(world, pos, facing);

		if (hasSignal && !state.getValue(EXTENDED)) {
			if ((new BlockReinforcedPistonStructureHelper(world, pos, facing, true)).canMove()) {
				world.addBlockEvent(pos, this, 0, facing.getIndex());
			}
		}
		else if (!hasSignal && state.getValue(EXTENDED)) {
			world.addBlockEvent(pos, this, 1, facing.getIndex());
		}
	}

	private boolean shouldBeExtended(World world, BlockPos pos, EnumFacing facing) { // copied because shouldBeExtended() in PistonBlock is private
		for (EnumFacing dir : EnumFacing.values()) {
			if (dir != facing && world.isSidePowered(pos.offset(dir), dir)) {
				return true;
			}
		}

		if (world.isSidePowered(pos, EnumFacing.DOWN)) {
			return true;
		}
		else {
			BlockPos posAbove = pos.up();

			for (EnumFacing dir : EnumFacing.values()) {
				if (dir != EnumFacing.DOWN && world.isSidePowered(posAbove.offset(dir), dir)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		EnumFacing facing = state.getValue(FACING);

		if (!world.isRemote) {
			boolean isPowered = shouldBeExtended(world, pos, facing);

			if (isPowered && id == 1) {
				world.setBlockState(pos, state.withProperty(EXTENDED, true), 2);
				return false;
			}

			if (!isPowered && id == 0) {
				return false;
			}
		}

		if (id == 0) {
			if (!doMove(world, pos, facing, true)) {
				return false;
			}

			world.setBlockState(pos, state.withProperty(EXTENDED, true), 3);
			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
		}
		else if (id == 1) {
			TileEntity tePiston = world.getTileEntity(pos.offset(facing));

			if (tePiston instanceof TileEntityReinforcedPiston) {
				((TileEntityReinforcedPiston)tePiston).clearPistonTileEntity();
			}

			TileEntity te = world.getTileEntity(pos);

			world.setBlockState(pos, SCContent.reinforcedPistonExtension.getDefaultState().withProperty(BlockPistonMoving.FACING, facing).withProperty(BlockPistonMoving.TYPE, isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
			world.setTileEntity(pos, BlockReinforcedPistonMoving.createTilePiston(getStateFromMeta(param), te != null ? te.getUpdateTag() : null, facing, false, true));

			if (isSticky) {
				BlockPos offsetPos = pos.add(facing.getXOffset() * 2, facing.getYOffset() * 2, facing.getZOffset() * 2);
				IBlockState offsetState = world.getBlockState(offsetPos);
				Block offsetBlock = offsetState.getBlock();
				boolean flag = false;

				if (offsetBlock == SCContent.reinforcedPistonExtension) {
					TileEntity offsetTe = world.getTileEntity(offsetPos);

					if (offsetTe instanceof TileEntityReinforcedPiston) {
						TileEntityReinforcedPiston offsetTePiston = (TileEntityReinforcedPiston)offsetTe;

						if (offsetTePiston.getFacing() == facing && offsetTePiston.isExtending()) {
							offsetTePiston.clearPistonTileEntity();
							flag = true;
						}
					}
				}

				if (!flag && !offsetState.getBlock().isAir(offsetState, world, offsetPos) && canPush(offsetState, world, pos, offsetPos, facing.getOpposite(), false, facing) && (offsetState.getPushReaction() == EnumPushReaction.NORMAL || offsetBlock == SCContent.reinforcedPiston || offsetBlock == SCContent.reinforcedStickyPiston)) {
					doMove(world, pos, facing, false);
				}
			}
			else {
				world.setBlockToAir(pos.offset(facing));
			}

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	public static boolean canPush(IBlockState state, World world, BlockPos pistonPos, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing direction) {
		Block block = state.getBlock();

		if (block == Blocks.OBSIDIAN || block == SCContent.reinforcedObsidian) {
			return false;
		}
		else if (!world.getWorldBorder().contains(pos)) {
			return false;
		}
		else if (pos.getY() >= 0 && (facing != EnumFacing.DOWN || pos.getY() != 0)) {
			if (pos.getY() <= world.getHeight() - 1 && (facing != EnumFacing.UP || pos.getY() != world.getHeight() - 1)) {
				if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON && block != SCContent.reinforcedPiston && block != SCContent.reinforcedStickyPiston) {
					if (state.getBlock() instanceof IReinforcedBlock) {
						if (!isSameOwner(pos, pistonPos, world)) {
							return false;
						}
					}
					else if (state.getBlockHardness(world, pos) == -1.0F) {
						return false;
					}

					switch (state.getPushReaction()) {
						case BLOCK:
							return false;
						case DESTROY:
							return destroyBlocks;
						case PUSH_ONLY:
							return facing == direction;
						default: break;
					}
				}
				else if (state.getValue(EXTENDED)) {
					return false;
				}

				return !block.hasTileEntity(state) || state.getBlock() instanceof IReinforcedBlock;
			}
		}

		return false;
	}

	private boolean doMove(World world, BlockPos pos, EnumFacing facing, boolean extending) {
		if (!extending) {
			world.setBlockToAir(pos.offset(facing));
		}

		BlockReinforcedPistonStructureHelper structureHelper = new BlockReinforcedPistonStructureHelper(world, pos, facing, extending);

		if (!structureHelper.canMove()) {
			return false;
		} else {
			List<BlockPos> blocksToMove = structureHelper.getBlocksToMove();
			List<IBlockState> statesToMove = Lists.newArrayList();

			for (int i = 0; i < blocksToMove.size(); ++i) {
				BlockPos posToMove = blocksToMove.get(i);

				statesToMove.add(world.getBlockState(posToMove).getActualState(world, posToMove));
			}

			List<BlockPos> blocksToDestroy = structureHelper.getBlocksToDestroy();
			int k = blocksToMove.size() + blocksToDestroy.size();
			IBlockState[] updatedBlocks = new IBlockState[k];
			EnumFacing direction = extending ? facing : facing.getOpposite();

			for (int j = blocksToDestroy.size() - 1; j >= 0; --j) {
				BlockPos posToDestroy = blocksToDestroy.get(j);
				IBlockState stateToDestroy = world.getBlockState(posToDestroy);
				// Forge: With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
				float chance = stateToDestroy.getBlock() instanceof BlockSnow ? -1.0f : 1.0f;

				stateToDestroy.getBlock().dropBlockAsItemWithChance(world, posToDestroy, stateToDestroy, chance, 0);
				world.setBlockState(posToDestroy, Blocks.AIR.getDefaultState(), 4);
				--k;
				updatedBlocks[k] = stateToDestroy;
			}

			for (int l = blocksToMove.size() - 1; l >= 0; --l) {
				BlockPos posToMove = blocksToMove.get(l);
				IBlockState stateToMove = world.getBlockState(posToMove);

				TileEntity teToMove = world.getTileEntity(posToMove);
				NBTTagCompound tag = null;

				if (teToMove != null){
					tag = new NBTTagCompound();
					teToMove.setPos(posToMove.offset(direction));
					teToMove.writeToNBT(tag);
				}

				world.setBlockState(posToMove, Blocks.AIR.getDefaultState(), 2);
				posToMove = posToMove.offset(direction);
				world.setBlockState(posToMove, SCContent.reinforcedPistonExtension.getDefaultState().withProperty(FACING, facing), 4);
				world.setTileEntity(posToMove, BlockReinforcedPistonMoving.createTilePiston(statesToMove.get(l), tag, facing, extending, false));
				--k;
				updatedBlocks[k] = stateToMove;
			}

			BlockPos frontPos = pos.offset(facing);
			TileEntity pistonTe = world.getTileEntity(pos);

			if (extending) {
				EnumPistonType type = isSticky ? EnumPistonType.STICKY : EnumPistonType.DEFAULT;
				IBlockState pistonHead = SCContent.reinforcedPistonHead.getDefaultState().withProperty(BlockPistonExtension.FACING, facing).withProperty(BlockPistonExtension.TYPE, type);
				IBlockState pistonExtension = SCContent.reinforcedPistonExtension.getDefaultState().withProperty(BlockPistonMoving.FACING, facing).withProperty(BlockPistonMoving.TYPE, isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
				TileEntityOwnable headTe = new TileEntityOwnable();

				if (pistonTe instanceof TileEntityOwnable) { //synchronize owner to the piston head
					headTe.setOwner(((TileEntityOwnable)pistonTe).getOwner().getUUID(), ((TileEntityOwnable)pistonTe).getOwner().getName());
				}

				world.setBlockState(frontPos, pistonExtension, 4);
				world.setTileEntity(frontPos, BlockReinforcedPistonMoving.createTilePiston(pistonHead, headTe.getUpdateTag(), facing, true, true));
			}

			for (int i1 = blocksToDestroy.size() - 1; i1 >= 0; --i1) {
				world.notifyNeighborsOfStateChange(blocksToDestroy.get(i1), updatedBlocks[k++].getBlock(), false);
			}

			for (int j1 = blocksToMove.size() - 1; j1 >= 0; --j1) {
				world.notifyNeighborsOfStateChange(blocksToMove.get(j1), updatedBlocks[k++].getBlock(), false);
			}

			if (extending) {
				world.notifyNeighborsOfStateChange(frontPos, SCContent.reinforcedPistonHead, false);
			}

			return true;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityValidationOwnable();
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON);
	}

	@Override
	public int getAmount() {
		return 0;
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
