package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AlarmBlock extends OwnableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool LIT = PropertyBool.create("lit");

	public AlarmBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		destroyTimeForOwner = 3.5F;
		setHarvestLevel("pickaxe", 0);
		blockMapColor = MapColor.RED;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof AlarmBlockEntity) {
			AlarmBlockEntity te = (AlarmBlockEntity) tile;

			if (te.isOwnedBy(player)) {
				if (!world.isRemote) {
					if (te.isDisabled())
						player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else
						SecurityCraft.network.sendTo(new OpenScreen(DataType.ALARM, pos), (EntityPlayerMP) player);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(LIT) ? 15 : 0;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.UP && world.isSideSolid(pos.down(), EnumFacing.UP) || world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side != null && side.getAxis() == Axis.Y;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!canPlaceBlockOnSide(world, pos, state.getValue(FACING))) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, world.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ? facing : EnumFacing.DOWN).withProperty(LIT, false);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote)
			world.scheduleUpdate(pos, state.getBlock(), 5);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (!world.isRemote) {
			updateState(world, pos, state);
			world.scheduleUpdate(pos, state.getBlock(), 5);
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess w, BlockPos pos, BlockPos neighbor) {
		World world = (World) w;

		if (world.isRemote)
			return;

		updateState(world, pos, world.getBlockState(pos));

		EnumFacing facing = world.getBlockState(pos).getValue(FACING);

		if (!world.isSideSolid(pos.offset(facing.getOpposite()), facing, true)) {
			dropBlockAsItem((world), pos, world.getBlockState(pos), 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (!ConfigHandler.vanillaToolBlockBreaking && te instanceof IModuleInventory)
			((IModuleInventory) te).dropAllModules();

		super.breakBlock(world, pos, state);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		float threePx = 0.1875F;
		float ySideMin = 0.5F - threePx; //bottom of the alarm when placed on a block side
		float ySideMax = 0.5F + threePx; //top of the alarm when placed on a block side
		float hSideMin = 0.5F - threePx; //the left start for s/w and right start for n/e
		float hSideMax = 0.5F + threePx; //the left start for n/e and right start for s/w
		float px = 1.0F / 16.0F; //one sixteenth of a block
		EnumFacing facing = state.getValue(FACING);

		switch (AlarmBlock.SwitchEnumFacing.FACING_LOOKUP[facing.ordinal()]) {
			case 1: //east
				return new AxisAlignedBB(0.0F, ySideMin - px, hSideMin - px, 0.5F, ySideMax + px, hSideMax + px);
			case 2: //west
				return new AxisAlignedBB(0.5F, ySideMin - px, hSideMin - px, 1.0F, ySideMax + px, hSideMax + px);
			case 3: //north
				return new AxisAlignedBB(hSideMin - px, ySideMin - px, 0.0F, hSideMax + px, ySideMax + px, 0.5F);
			case 4: //south
				return new AxisAlignedBB(hSideMin - px, ySideMin - px, 0.5F, hSideMax + px, ySideMax + px, 1.0F);
			case 5: //up
				return new AxisAlignedBB(0.5F - threePx - px, 0F, 0.5F - threePx - px, 0.5F + threePx + px, 0.5F, 0.5F + threePx + px);
			case 6: //down
				return new AxisAlignedBB(0.5F - threePx - px, 0.5F, 0.5F - threePx - px, 0.5F + threePx + px, 1.0F, 0.5F + threePx + px);
			default:
				return state.getBoundingBox(source, pos);
		}
	}

	private void updateState(World world, BlockPos pos, IBlockState state) {
		if (state.getBlock() != SCContent.alarm)
			return;

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof AlarmBlockEntity) {
			AlarmBlockEntity te = (AlarmBlockEntity) tile;

			if (world.getRedstonePowerFromNeighbors(pos) > 0) {
				boolean isPowered = te.isPowered();

				if (!isPowered) {
					world.setBlockState(pos, state.withProperty(LIT, true));
					te.setPowered(true);
				}
			}
			else {
				boolean isPowered = te.isPowered();

				if (isPowered) {
					world.setBlockState(pos, state.withProperty(LIT, false));
					te.setPowered(false);
				}
			}
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing;
		boolean lit = meta > 6;

		if (lit)
			meta = meta - 6;

		switch (meta & 7) {
			case 0:
				facing = EnumFacing.DOWN;
				break;
			case 1:
				facing = EnumFacing.EAST;
				break;
			case 2:
				facing = EnumFacing.WEST;
				break;
			case 3:
				facing = EnumFacing.SOUTH;
				break;
			case 4:
				facing = EnumFacing.NORTH;
				break;
			case 5:
			default:
				facing = EnumFacing.UP;
		}

		return getDefaultState().withProperty(FACING, facing).withProperty(LIT, lit);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta;

		switch (AlarmBlock.SwitchEnumFacing.FACING_LOOKUP[state.getValue(FACING).ordinal()]) {
			case 1:
				meta = 1;
				break;
			case 2:
				meta = 2;
				break;
			case 3:
				meta = 3;
				break;
			case 4:
				meta = 4;
				break;
			case 6:
				meta = 0;
				break;
			case 5:
			default:
				meta = 5;
		}

		if (state.getValue(LIT))
			meta += 6;

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, LIT);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		EnumFacing facing = state.getValue(FACING);

		switch (mirror) {
			case LEFT_RIGHT:
				if (facing.getAxis() == Axis.Z)
					return state.withProperty(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if (facing.getAxis() == Axis.X)
					return state.withProperty(FACING, facing.getOpposite());
				break;
			case NONE:
				break;
		}

		return state;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new AlarmBlockEntity();
	}

	static final class SwitchEnumFacing {
		static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

		static {
			try {
				FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 1;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 2;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 3;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 4;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.UP.ordinal()] = 5;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 6;
			}
			catch (NoSuchFieldError e) {}
		}

		private SwitchEnumFacing() {}
	}
}
