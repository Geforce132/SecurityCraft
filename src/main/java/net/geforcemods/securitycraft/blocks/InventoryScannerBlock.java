package net.geforcemods.securitycraft.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.BlockUtils;
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
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class InventoryScannerBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool HORIZONTAL = PropertyBool.create("horizontal");

	public InventoryScannerBlock(Material material) {
		super(material);
		setSoundType(SoundType.STONE);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HORIZONTAL, false));
		destroyTimeForOwner = 3.5F;
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		setDefaultFacing(world, pos, state);
	}

	private void setDefaultFacing(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			IBlockState north = world.getBlockState(pos.north());
			IBlockState south = world.getBlockState(pos.south());
			IBlockState west = world.getBlockState(pos.west());
			IBlockState east = world.getBlockState(pos.east());
			EnumFacing facing = state.getValue(FACING);

			if (facing == EnumFacing.NORTH && north.isFullBlock() && !south.isFullBlock())
				facing = EnumFacing.SOUTH;
			else if (facing == EnumFacing.SOUTH && south.isFullBlock() && !north.isFullBlock())
				facing = EnumFacing.NORTH;
			else if (facing == EnumFacing.WEST && west.isFullBlock() && !east.isFullBlock())
				facing = EnumFacing.EAST;
			else if (facing == EnumFacing.EAST && east.isFullBlock() && !west.isFullBlock())
				facing = EnumFacing.WEST;

			world.setBlockState(pos, state.withProperty(FACING, facing), 2);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (isFacingAnotherScanner(world, pos)) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof InventoryScannerBlockEntity) {
				InventoryScannerBlockEntity te = (InventoryScannerBlockEntity) tile;

				if (te.isDisabled())
					player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
				else
					player.openGui(SecurityCraft.instance, Screens.INVENTORY_SCANNER.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize("tile.securitycraft:inventoryScanner.name"), Utils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		if (world.isRemote)
			return;

		setDefaultFacing(world, pos, state);
		checkAndPlaceAppropriately(world, pos, entity, false);
	}

	public static void checkAndPlaceAppropriately(World world, BlockPos pos, EntityLivingBase player, boolean force) {
		InventoryScannerBlockEntity connectedScanner = getConnectedInventoryScanner(world, pos);
		InventoryScannerBlockEntity thisTe = (InventoryScannerBlockEntity) world.getTileEntity(pos);

		if (connectedScanner == null)
			return;

		if (!force) {
			if (connectedScanner.isDisabled()) {
				thisTe.setDisabled(true);
				return;
			}
		}
		else {
			thisTe.setDisabled(false);
			connectedScanner.setDisabled(false);
		}

		if (player instanceof EntityPlayer) {
			if (!connectedScanner.isOwnedBy(player))
				return;
		}
		else if (!connectedScanner.getOwner().owns(thisTe))
			return;

		boolean horizontal = false;

		if (world.getBlockState(connectedScanner.getPos()).getValue(HORIZONTAL))
			horizontal = true;

		thisTe.setHorizontal(horizontal);

		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
		int loopBoundary = facing == EnumFacing.WEST || facing == EnumFacing.EAST ? Math.abs(pos.getX() - connectedScanner.getPos().getX()) : (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getPos().getZ()) : 0);

		for (int i = 1; i < loopBoundary; i++) {
			if (world.getBlockState(pos.offset(facing, i)).getBlock() == SCContent.inventoryScannerField)
				return;
		}

		Option<?>[] customOptions = thisTe.customOptions();

		for (int i = 1; i < loopBoundary; i++) {
			BlockPos offsetPos = pos.offset(facing, i);

			world.setBlockState(offsetPos, SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, facing).withProperty(HORIZONTAL, horizontal));

			TileEntity te = world.getTileEntity(offsetPos);

			if (te instanceof IOwnable)
				((IOwnable) te).setOwner(thisTe.getOwner().getUUID(), thisTe.getOwner().getName());
		}

		for (ModuleType type : connectedScanner.getInsertedModules()) {
			thisTe.insertModule(connectedScanner.getModule(type), false);
		}

		((BooleanOption) customOptions[0]).setValue(connectedScanner.isHorizontal());
		((BooleanOption) customOptions[1]).setValue(connectedScanner.doesFieldSolidify());
		((BooleanOption) customOptions[2]).setValue(false);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (world.getBlockState(fromPos).getBlock() != SCContent.inventoryScannerField)
			checkAndPlaceAppropriately(world, pos, null, false);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote)
			return;

		InventoryScannerBlockEntity connectedScanner = getConnectedInventoryScanner(world, pos, state, null);
		TileEntity tile = world.getTileEntity(pos);

		BlockUtils.removeInSequence((direction, stateToCheck) -> {
			if (stateToCheck.getBlock() != SCContent.inventoryScannerField)
				return false;

			EnumFacing stateToCheckFacing = stateToCheck.getValue(FACING);

			return stateToCheckFacing == direction || stateToCheckFacing == direction.getOpposite();
		}, world, pos, state.getValue(FACING));

		if (tile instanceof InventoryScannerBlockEntity) {
			InventoryScannerBlockEntity te = (InventoryScannerBlockEntity) tile;

			//first 10 slots (0-9) are the prohibited slots
			for (int i = 10; i < te.getSizeInventory(); i++) {
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getContents().get(i));
			}

			InventoryHelper.dropInventoryItems(world, pos, te.getLensContainer());

			if (te.isProvidingPower()) {
				world.notifyNeighborsOfStateChange(pos, this, false);
				BlockUtils.updateIndirectNeighbors(world, pos, this);
			}
		}

		if (connectedScanner != null) {
			for (int i = 0; i < connectedScanner.getContents().size(); i++) {
				connectedScanner.getContents().set(i, ItemStack.EMPTY);
			}

			connectedScanner.getLensContainer().clear();
		}

		super.breakBlock(world, pos, state);
	}

	private boolean isFacingAnotherScanner(World world, BlockPos pos) {
		return getConnectedInventoryScanner(world, pos) != null;
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(World world, BlockPos pos) {
		return getConnectedInventoryScanner(world, pos, world.getBlockState(pos), null);
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(World world, BlockPos pos, IBlockState stateAtPos, Consumer<OwnableBlockEntity> fieldModifier) {
		if (stateAtPos.getBlock() != SCContent.inventoryScanner && stateAtPos.getBlock() != SCContent.inventoryScannerField)
			return null;

		EnumFacing facing = stateAtPos.getValue(FACING);
		List<BlockPos> fields = new ArrayList<>();

		for (int i = 0; i <= ConfigHandler.inventoryScannerRange; i++) {
			BlockPos offsetPos = pos.offset(facing, i);
			IBlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();
			boolean isField = block == SCContent.inventoryScannerField;

			if (!isField && !state.getBlock().isAir(state, world, offsetPos) && !state.getBlock().isReplaceable(world, pos) && block != SCContent.inventoryScanner)
				return null;

			if (isField)
				fields.add(offsetPos);

			if (block == SCContent.inventoryScanner && state.getValue(FACING) == facing.getOpposite()) {
				if (fieldModifier != null)
					fields.stream().map(world::getTileEntity).forEach(be -> fieldModifier.accept((OwnableBlockEntity) be));

				return (InventoryScannerBlockEntity) world.getTileEntity(offsetPos);
			}
		}

		return null;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntity te = blockAccess.getTileEntity(pos);

		if (!(te instanceof InventoryScannerBlockEntity))
			return 0;

		return ((InventoryScannerBlockEntity) te).isProvidingPower() ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta % 4)).withProperty(HORIZONTAL, meta > 3);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getBlock() != this ? 0 : state.getValue(FACING).getHorizontalIndex() + (state.getValue(HORIZONTAL) ? 4 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, HORIZONTAL);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new InventoryScannerBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	public static class DoorActivator implements Function<Object, IDoorActivator>, IDoorActivator {
		private List<Block> blocks = Arrays.asList(SCContent.inventoryScanner);

		@Override
		public IDoorActivator apply(Object o) {
			return this;
		}

		@Override
		public boolean isPowering(World world, BlockPos pos, IBlockState state, TileEntity te, EnumFacing direction, int distance) {
			return ((InventoryScannerBlockEntity) te).isProvidingPower();
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
