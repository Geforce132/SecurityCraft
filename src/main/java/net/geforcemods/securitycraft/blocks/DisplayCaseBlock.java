package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.AttachFace;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class DisplayCaseBlock extends OwnableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<AttachFace> ATTACH_FACE = PropertyEnum.create("attach_face", AttachFace.class);
	public static final AxisAlignedBB FLOOR;
	public static final AxisAlignedBB CEILING;
	public static final AxisAlignedBB WALL_N;
	public static final AxisAlignedBB WALL_E;
	public static final AxisAlignedBB WALL_S;
	public static final AxisAlignedBB WALL_W;

	static {
		float px = 1.0F / 16.0F;

		FLOOR = new AxisAlignedBB(2 * px, 0 * px, 2 * px, 14 * px, 6 * px, 14 * px);
		CEILING = new AxisAlignedBB(2 * px, 10 * px, 2 * px, 14 * px, 16 * px, 14 * px);
		WALL_N = new AxisAlignedBB(2 * px, 2 * px, 10 * px, 14 * px, 14 * px, 16 * px);
		WALL_E = new AxisAlignedBB(0 * px, 2 * px, 2 * px, 6 * px, 14 * px, 14 * px);
		WALL_S = new AxisAlignedBB(2 * px, 2 * px, 0 * px, 14 * px, 14 * px, 6 * px);
		WALL_W = new AxisAlignedBB(10 * px, 2 * px, 2 * px, 16 * px, 14 * px, 14 * px);
	}

	public DisplayCaseBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ATTACH_FACE, AttachFace.WALL));
		setHardness(5.0F);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		switch (state.getValue(ATTACH_FACE)) {
			case FLOOR:
				return FLOOR;
			case CEILING:
				return CEILING;
			case WALL:
				switch (state.getValue(FACING)) {
					case NORTH:
						return WALL_N;
					case EAST:
						return WALL_E;
					case SOUTH:
						return WALL_S;
					case WEST:
						return WALL_W;
					default:
						return NULL_AABB;
				}
		}

		return NULL_AABB;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		if (canPlaceBlockOnSide(world, pos, facing)) {
			if (facing.getAxis() == Axis.Y)
				return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(ATTACH_FACE, facing == EnumFacing.UP ? AttachFace.FLOOR : AttachFace.CEILING);
			else
				return getDefaultState().withProperty(FACING, facing).withProperty(ATTACH_FACE, AttachFace.WALL);
		}

		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos.offset(side.getOpposite())).isSideSolid(world, pos, side);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		EnumFacing connectedDirection = getConnectedDirection(state);

		if (!canPlaceBlockOnSide(world, pos, connectedDirection)) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof DisplayCaseBlockEntity) {
				DisplayCaseBlockEntity be = (DisplayCaseBlockEntity) te;
				ItemStack heldStack = player.getHeldItem(hand);

				if (be.isLocked() && be.disableInteractionWhenLocked(world, pos, player)) {
					TextComponentTranslation blockName = Utils.localize(getTranslationKey());

					PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
					return true;
				}

				if (be.isOpen()) {
					ItemStack displayedStack = be.getDisplayedStack();

					if (displayedStack.isEmpty()) {
						if (!heldStack.isEmpty()) {
							ItemStack toAdd;

							if (player.isCreative()) {
								toAdd = heldStack.copy();
								toAdd.setCount(1);
							}
							else
								toAdd = heldStack.splitStack(1);

							be.setDisplayedStack(toAdd);
							world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
							return true;
						}
					}
					else if (player.isSneaking()) {
						player.addItemStackToInventory(displayedStack);
						world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
						be.setDisplayedStack(ItemStack.EMPTY);
						return true;
					}

					be.setOpen(false);
				}
				else {
					if (be.isDisabled())
						player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else if (be.verifyPasscodeSet(world, pos, be, player)) {
						if (be.isDenied(player)) {
							if (be.sendsDenylistMessage())
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
						}
						else if (be.isAllowed(player)) {
							if (be.sendsAllowlistMessage())
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

							activate(be);
						}
						else if (player.getHeldItem(hand).getItem() != SCContent.codebreaker)
							be.openPasscodeGUI(world, pos, player);
					}
				}
			}
		}

		return true;
	}

	public void activate(DisplayCaseBlockEntity be) {
		be.setOpen(true);
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

		if (te instanceof DisplayCaseBlockEntity) {
			DisplayCaseBlockEntity be = (DisplayCaseBlockEntity) te;

			if (!ConfigHandler.vanillaToolBlockBreaking)
				be.dropAllModules();

			Block.spawnAsEntity(world, pos, be.getDisplayedStack());
		}

		if (te instanceof IPasscodeProtected)
			SaltData.removeSalt(((IPasscodeProtected) te).getSaltKey());

		super.breakBlock(world, pos, state);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Arrays.asList(new ItemStack(SCContent.displayCase));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof DisplayCaseBlockEntity) {
			DisplayCaseBlockEntity be = (DisplayCaseBlockEntity) te;
			ItemStack displayedStack = be.getDisplayedStack();

			if (!displayedStack.isEmpty() && be.isOpen() && !GuiScreen.isCtrlKeyDown())
				return displayedStack;
		}

		return new ItemStack(SCContent.displayCase);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)).withProperty(ATTACH_FACE, meta >= 8 ? AttachFace.CEILING : (meta >= 4 ? AttachFace.FLOOR : AttachFace.WALL));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		AttachFace attachFace = state.getValue(ATTACH_FACE);

		return state.getValue(FACING).getHorizontalIndex() + (attachFace == AttachFace.WALL ? 0 : (attachFace == AttachFace.FLOOR ? 4 : 8));
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
		return new DisplayCaseBlockEntity();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ATTACH_FACE);
	}

	private EnumFacing getConnectedDirection(IBlockState state) {
		switch (state.getValue(ATTACH_FACE)) {
			case FLOOR:
				return EnumFacing.UP;
			case CEILING:
				return EnumFacing.DOWN;
			case WALL:
			default:
				return state.getValue(FACING);
		}
	}
}
