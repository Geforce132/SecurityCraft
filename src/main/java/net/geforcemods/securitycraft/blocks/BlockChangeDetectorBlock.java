package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockChangeDetectorBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	protected BlockChangeDetectorBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
		destroyTimeForOwner = 3.5F;
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof BlockChangeDetectorBlockEntity))
			return false;

		BlockChangeDetectorBlockEntity te = (BlockChangeDetectorBlockEntity) tile;

		if (te.isOwnedBy(player) || te.isAllowed(player)) {
			player.openGui(SecurityCraft.instance, Screens.BLOCK_CHANGE_DETECTOR.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}

		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof BlockChangeDetectorBlockEntity) {
			EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), ((BlockChangeDetectorBlockEntity) te).getStackInSlot(36));

			Utils.addScheduledTask(world, () -> world.spawnEntity(item));
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (state.getValue(POWERED)) {
			world.setBlockState(pos, state.withProperty(POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, this, getConnectedDirection(state).getOpposite());
		}
	}

	public abstract EnumFacing getConnectedDirection(IBlockState state);

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) && getConnectedDirection(state) == side ? 15 : 0;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BlockChangeDetectorBlockEntity();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Arrays.asList(new ItemStack(SCContent.blockChangeDetectorItem));
	}

	@Override
	public ItemStack getDefaultStack() {
		return new ItemStack(SCContent.blockChangeDetectorItem);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return getDisguisedStack(world, pos).getItem() == SCContent.blockChangeDetectorItem;
	}
}
