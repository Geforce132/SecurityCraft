package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
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

public class BlockInventoryScanner extends BlockDisguisable {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool HORIZONTAL = PropertyBool.create("horizontal");

	public BlockInventoryScanner(Material material) {
		super(material);
		setSoundType(SoundType.STONE);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HORIZONTAL, false));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(world, pos, state);
		setDefaultFacing(world, pos, state);
	}

	private void setDefaultFacing(World world, BlockPos pos, IBlockState state)
	{
		if (!world.isRemote)
		{
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(isFacingAnotherScanner(world, pos))
			player.openGui(SecurityCraft.instance, GuiHandler.INVENTORY_SCANNER_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize("tile.securitycraft:inventoryScanner.name"), Utils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);

		return true;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		if(world.isRemote)
			return;

		setDefaultFacing(world, pos, state);
		checkAndPlaceAppropriately(world, pos, entity);
	}

	private void checkAndPlaceAppropriately(World world, BlockPos pos, EntityLivingBase player)
	{
		TileEntityInventoryScanner connectedScanner = getConnectedInventoryScanner(world, pos);
		TileEntityInventoryScanner thisTe = (TileEntityInventoryScanner)world.getTileEntity(pos);

		if(connectedScanner == null)
			return;

		if(player instanceof EntityPlayer)
		{
			if(!connectedScanner.getOwner().isOwner((EntityPlayer)player))
				return;
		}
		else if(!connectedScanner.getOwner().owns(thisTe))
			return;

		boolean horizontal = false;

		if(world.getBlockState(connectedScanner.getPos()).getValue(HORIZONTAL))
			horizontal = true;

		thisTe.setHorizontal(horizontal);

		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
		int loopBoundary = facing == EnumFacing.WEST || facing == EnumFacing.EAST ? Math.abs(pos.getX() - connectedScanner.getPos().getX()) : (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getPos().getZ()) : 0);

		for(int i = 1; i < loopBoundary; i++)
		{
			if(world.getBlockState(pos.offset(facing, i)).getBlock() == SCContent.inventoryScannerField)
				return;
		}

		Option<?>[] customOptions = thisTe.customOptions();

		for(int i = 1; i < loopBoundary; i++)
		{
			BlockPos offsetPos = pos.offset(facing, i);

			world.setBlockState(offsetPos, SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, facing).withProperty(HORIZONTAL, horizontal));

			TileEntity te = world.getTileEntity(offsetPos);

			if(te instanceof IOwnable)
				((IOwnable)te).setOwner(thisTe.getOwner().getUUID(), thisTe.getOwner().getName());
		}

		for(EnumModuleType type : connectedScanner.getInsertedModules())
		{
			thisTe.insertModule(connectedScanner.getModule(type));
		}

		((OptionBoolean)customOptions[0]).setValue(connectedScanner.isHorizontal());
		((OptionBoolean)customOptions[1]).setValue(connectedScanner.doesFieldSolidify());
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if(world.getBlockState(fromPos).getBlock() != SCContent.inventoryScannerField)
			checkAndPlaceAppropriately(world, pos, null);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if(world.isRemote)
			return;

		TileEntityInventoryScanner connectedScanner = null;

		for(EnumFacing facing : EnumFacing.HORIZONTALS)
		{
			for(int i = 1; i <= ConfigHandler.inventoryScannerRange; i++)
			{
				BlockPos offsetIPos = pos.offset(facing, i);

				if(world.getBlockState(offsetIPos).getBlock() == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						BlockPos offsetJPos = pos.offset(facing, j);
						IBlockState field = world.getBlockState(offsetJPos);

						//checking if the field is oriented correctly
						if(field.getBlock() == SCContent.inventoryScannerField)
						{
							if(facing == EnumFacing.WEST || facing == EnumFacing.EAST)
							{
								if(field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.WEST || field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.EAST)
									world.destroyBlock(offsetJPos, false);
							}
							else if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
							{
								if(field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.NORTH || field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.SOUTH)
									world.destroyBlock(offsetJPos, false);
							}
						}
					}

					connectedScanner = (TileEntityInventoryScanner)world.getTileEntity(offsetIPos);
					break;
				}
			}
		}

		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof TileEntityInventoryScanner)
		{
			TileEntityInventoryScanner te = (TileEntityInventoryScanner)tile;

			for(int i = 10; i < te.getSizeInventory(); i++) //first 10 slots (0-9) are the prohibited slots
			{
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getContents().get(i));
			}
		}

		if(connectedScanner != null)
		{
			for(int i = 0; i < connectedScanner.getContents().size(); i++)
			{
				connectedScanner.getContents().set(i, ItemStack.EMPTY);
			}
		}

		super.breakBlock(world, pos, state);
	}

	private boolean isFacingAnotherScanner(World world, BlockPos pos)
	{
		return getConnectedInventoryScanner(world, pos) != null;
	}

	public static TileEntityInventoryScanner getConnectedInventoryScanner(World world, BlockPos pos)
	{
		EnumFacing facing = world.getBlockState(pos).getValue(FACING);

		for(int i = 0; i <= ConfigHandler.inventoryScannerRange; i++)
		{
			BlockPos offsetPos = pos.offset(facing, i);
			IBlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();

			if(!state.getBlock().isAir(state, world, offsetPos) && block != SCContent.inventoryScannerField && block != SCContent.inventoryScanner)
				return null;

			if(block == SCContent.inventoryScanner && state.getValue(FACING) == facing.getOpposite())
				return (TileEntityInventoryScanner)world.getTileEntity(offsetPos);
		}

		return null;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		if(!(blockAccess.getTileEntity(pos) instanceof TileEntityInventoryScanner))
			return 0 ;

		return (((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).hasModule(EnumModuleType.REDSTONE) && ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta % 4)).withProperty(HORIZONTAL, meta > 3);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getBlock() != this ? 0 : state.getValue(FACING).getHorizontalIndex() + (state.getValue(HORIZONTAL) ? 4 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, HORIZONTAL);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityInventoryScanner();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	public static class DoorActivator implements Function<Object,IDoorActivator>, IDoorActivator
	{
		private List<Block> blocks = Arrays.asList(SCContent.inventoryScanner);

		@Override
		public IDoorActivator apply(Object o)
		{
			return this;
		}

		@Override
		public boolean isPowering(World world, BlockPos pos, IBlockState state, TileEntity te)
		{
			return ((TileEntityInventoryScanner)te).hasModule(EnumModuleType.REDSTONE) && ((TileEntityInventoryScanner)te).shouldProvidePower();
		}

		@Override
		public List<Block> getBlocks()
		{
			return blocks;
		}
	}
}
