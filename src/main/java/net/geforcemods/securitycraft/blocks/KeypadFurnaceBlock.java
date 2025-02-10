package net.geforcemods.securitycraft.blocks;

import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class KeypadFurnaceBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool OPEN = PropertyBool.create("open");
	public static final PropertyBool LIT = PropertyBool.create("lit");
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(0, 0, 0.125F, 1, 1, 1);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(0, 0, 0, 0.875F, 1, 1);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(0, 0, 0, 1, 1, 0.875F);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0.125F, 0, 0, 1, 1, 1);

	public KeypadFurnaceBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, false).withProperty(LIT, false));
		setSoundType(SoundType.METAL);
		setHardness(5.0F);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		switch (state.getValue(FACING)) {
			case NORTH:
				return NORTH;
			case EAST:
				return EAST;
			case SOUTH:
				return SOUTH;
			case WEST:
				return WEST;
			default:
				return FULL_BLOCK_AABB;
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof IInventory) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory) te);
			world.updateComparatorOutputLevel(pos, this);
		}

		if (te instanceof IPasscodeProtected)
			SaltData.removeSalt(((IPasscodeProtected) te).getSaltKey());

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			KeypadFurnaceBlockEntity be = (KeypadFurnaceBlockEntity) world.getTileEntity(pos);

			if (be.isDisabled())
				player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(world, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, world, pos, player);
				}
				else if (player.getHeldItem(hand).getItem() != SCContent.codebreaker)
					be.openPasscodeGUI(world, pos, player);
			}
		}

		return true;
	}

	public void activate(IBlockState state, World world, BlockPos pos, EntityPlayer player) {
		if (!state.getValue(OPEN))
			world.setBlockState(pos, state.withProperty(OPEN, true));

		world.playEvent(null, Constants.WorldEvents.IRON_DOOR_OPEN_SOUND, pos, 0);
		player.openGui(SecurityCraft.instance, Screens.KEYPAD_FURNACE.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(LIT) ? 13 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(OPEN, false);
	}

	//@formatter:off
	/*
	 * lit property was added later, it needed to be jammed between the existing meta values that were wrongly assigned to start with
	 * meta - facing/open/lit
	 * 0 - north/false/true
	 * 1 - south/false/true
	 * 2 - north/false/false
	 * 3 - south/false/false
	 * 4 - west/false/false
	 * 5 - east/false/false
	 * 6 - west/false/true
	 * 7 - east/false/true
	 * 8 - north/true/false
	 * 9 - south/true/false
	 * 10 - west/true/false
	 * 11 - east/true/false
	 * 12 - north/true/true
	 * 13 - south/true/true
	 * 14 - west/true/true
	 * 15 - east/true/true
	 */
	//@formatter:on

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean open = meta >= 8;
		boolean lit = false;

		if (meta >= 12) {
			meta -= 4;
			lit = true;
		}
		else if (meta <= 1) {
			meta += 2;
			lit = true;
		}
		else if (meta == 6 || meta == 7) {
			meta -= 2;
			lit = true;
		}

		if (open)
			meta -= 6;

		return getDefaultState().withProperty(FACING, EnumFacing.values()[meta]).withProperty(OPEN, open).withProperty(LIT, lit);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getValue(OPEN))
			return state.getValue(FACING).getIndex() + 6 + (state.getValue(LIT) ? 4 : 0);
		else {
			EnumFacing facing = state.getValue(FACING);

			if (state.getValue(LIT)) {
				if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
					return facing.getIndex() - 2;
				else
					return facing.getIndex() + 2;
			}
			else
				return facing.getIndex();
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, OPEN, LIT);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new KeypadFurnaceBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	public static class Convertible implements Function<Object, IPasscodeConvertible>, IPasscodeConvertible {
		@Override
		public IPasscodeConvertible apply(Object o) {
			return this;
		}

		@Override
		public boolean isUnprotectedBlock(IBlockState state) {
			return state.getBlock() == Blocks.FURNACE || state.getBlock() == Blocks.LIT_FURNACE;
		}

		@Override
		public boolean isProtectedBlock(IBlockState state) {
			return state.getBlock() == SCContent.keypadFurnace;
		}

		@Override
		public boolean protect(EntityPlayer player, World level, BlockPos pos) {
			return convert(player, level, pos, true);
		}

		@Override
		public boolean unprotect(EntityPlayer player, World level, BlockPos pos) {
			return convert(player, level, pos, false);
		}

		public boolean convert(EntityPlayer player, World world, BlockPos pos, boolean protect) {
			IBlockState oldState = world.getBlockState(pos);
			boolean isLit;

			if (protect)
				isLit = oldState.getBlock() == Blocks.LIT_FURNACE;
			else
				isLit = oldState.getValue(LIT);

			Block convertedBlock = protect ? SCContent.keypadFurnace : (isLit ? Blocks.LIT_FURNACE : Blocks.FURNACE);
			EnumFacing facing = oldState.getValue(FACING);
			IBlockState convertedState = convertedBlock.getDefaultState().withProperty(FACING, facing);
			TileEntity be = world.getTileEntity(pos);
			NBTTagCompound tag;
			TileEntity newTe;

			if (protect)
				convertedState = convertedState.withProperty(OPEN, false).withProperty(LIT, isLit);
			else
				((IModuleInventory) be).dropAllModules();

			tag = be.writeToNBT(new NBTTagCompound());
			((IInventory) be).clear();
			world.setBlockState(pos, convertedState);
			newTe = world.getTileEntity(pos);
			newTe.readFromNBT(tag);

			if (protect && player != null)
				((IOwnable) newTe).setOwner(player.getUniqueID().toString(), player.getName());

			return true;
		}
	}
}
