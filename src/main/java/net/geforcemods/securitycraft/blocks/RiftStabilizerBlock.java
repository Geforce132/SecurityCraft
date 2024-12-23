package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class RiftStabilizerBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyEnum<EnumDoorHalf> HALF = BlockDoor.HALF;
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	private static final AxisAlignedBB SHAPE_UPPER = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D);

	public RiftStabilizerBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, EnumDoorHalf.LOWER).withProperty(POWERED, false));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (state.getValue(POWERED)) {
			world.setBlockState(pos, state.withProperty(POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, this);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = getDisguisedBlockState(world.getTileEntity(pos));

		if (actualState != null && actualState.getBlock() != this)
			return actualState.getBoundingBox(world, pos);
		else
			return state.getValue(HALF) == EnumDoorHalf.UPPER ? SHAPE_UPPER : Block.FULL_BLOCK_AABB;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof RiftStabilizerBlockEntity) {
			RiftStabilizerBlockEntity riftStabilizer = ((RiftStabilizerBlockEntity) te);

			if (riftStabilizer.isOwnedBy(player)) {
				if (!world.isRemote) {
					if (riftStabilizer.isDisabled())
						player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else
						SecurityCraft.network.sendTo(new OpenScreen(DataType.RIFT_STABILIZER, pos), (EntityPlayerMP) player);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
			BlockPos lowerPos = pos.down();
			IBlockState lowerState = world.getBlockState(lowerPos);

			if (lowerState.getBlock() != this)
				world.setBlockToAir(pos);
			else if (block != this)
				lowerState.neighborChanged(world, lowerPos, block, fromPos);
		}
		else {
			boolean shouldDrop = false;
			BlockPos upperPos = pos.up();
			IBlockState upperState = world.getBlockState(upperPos);

			if (upperState.getBlock() != this) {
				world.setBlockToAir(pos);
				shouldDrop = true;
			}

			if (shouldDrop && !world.isRemote)
				dropBlockAsItem(world, pos, state, 0);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && pos.getY() < world.getHeight() - 1 && world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up());
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		BlockPos posAbove = pos.up();

		world.setBlockState(posAbove, getDefaultState().withProperty(HALF, EnumDoorHalf.UPPER), 3);

		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, posAbove, ((EntityPlayer) placer)));

		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		BlockPos lowerPos = pos.down();
		BlockPos upperPos = pos.up();

		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		if (player.capabilities.isCreativeMode && state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER && world.getBlockState(lowerPos).getBlock() == this)
			world.setBlockToAir(lowerPos);

		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER && world.getBlockState(upperPos).getBlock() == this) {
			if (player.capabilities.isCreativeMode)
				world.setBlockToAir(pos);

			world.setBlockToAir(upperPos);
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() + (state.getValue(POWERED) ? 4 : 0) + (state.getValue(HALF) == EnumDoorHalf.UPPER ? 8 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumDoorHalf half = meta >= 8 ? EnumDoorHalf.UPPER : EnumDoorHalf.LOWER;
		boolean powered = (meta & 4) > 0;

		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)).withProperty(HALF, half).withProperty(POWERED, powered);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : Item.getItemFromBlock(this);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);

		return state.getValue(POWERED) && state.getValue(HALF) == EnumDoorHalf.LOWER && te instanceof RiftStabilizerBlockEntity ? 15 - (int) ((RiftStabilizerBlockEntity) te).getLastTeleportDistance() : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getWeakPower(state, world, pos, side);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if (state.getValue(HALF) == EnumDoorHalf.LOWER && te instanceof RiftStabilizerBlockEntity) {
			RiftStabilizerBlockEntity riftStabilizer = ((RiftStabilizerBlockEntity) te);

			return riftStabilizer.isModuleEnabled(ModuleType.REDSTONE) && riftStabilizer.getLastTeleportationType() != null ? riftStabilizer.getComparatorOutputFunction().applyAsInt(riftStabilizer.getLastTeleportationType()) : 0;
		}

		return 0;
	}

	public static RiftStabilizerBlockEntity getConnectedTileEntity(World level, BlockPos pos) {
		IBlockState state = level.getBlockState(pos);
		BlockPos connectedPos = state.getValue(HALF) == EnumDoorHalf.LOWER ? pos.up() : pos.down();
		TileEntity te = level.getTileEntity(connectedPos);

		return te instanceof RiftStabilizerBlockEntity ? ((RiftStabilizerBlockEntity) te) : null;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, HALF, POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new RiftStabilizerBlockEntity();
	}
}
