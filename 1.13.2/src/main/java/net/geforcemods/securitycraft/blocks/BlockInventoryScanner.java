package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.TEInteractionObject;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockInventoryScanner extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockInventoryScanner(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.STONE));
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
			EnumFacing facing = state.get(FACING);

			if (facing == EnumFacing.NORTH && north.isFullCube() && !south.isFullCube())
				facing = EnumFacing.SOUTH;
			else if (facing == EnumFacing.SOUTH && south.isFullCube() && !north.isFullCube())
				facing = EnumFacing.NORTH;
			else if (facing == EnumFacing.WEST && west.isFullCube() && !east.isFullCube())
				facing = EnumFacing.EAST;
			else if (facing == EnumFacing.EAST && east.isFullCube() && !west.isFullCube())
				facing = EnumFacing.WEST;

			world.setBlockState(pos, state.with(FACING, facing), 2);
		}
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		else{
			if(isFacingAnotherScanner(world, pos) && player instanceof EntityPlayerMP)
				NetworkHooks.openGui((EntityPlayerMP)player, new TEInteractionObject(GuiHandler.INVENTORY_SCANNER, world, pos), pos);
			else
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securitycraft:inventoryScanner.name"), ClientUtils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);

			return true;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack){
		if(world.isRemote)
			return;

		IBlockState north = world.getBlockState(pos.north());
		IBlockState south = world.getBlockState(pos.south());
		IBlockState west = world.getBlockState(pos.west());
		IBlockState east = world.getBlockState(pos.east());
		EnumFacing facing = state.get(FACING);

		if (facing == EnumFacing.NORTH && north.isFullCube() && !south.isFullCube())
			facing = EnumFacing.SOUTH;
		else if (facing == EnumFacing.SOUTH && south.isFullCube() && !north.isFullCube())
			facing = EnumFacing.NORTH;
		else if (facing == EnumFacing.WEST && west.isFullCube() && !east.isFullCube())
			facing = EnumFacing.EAST;
		else if (facing == EnumFacing.EAST && east.isFullCube() && !west.isFullCube())
			facing = EnumFacing.WEST;

		world.setBlockState(pos, state.with(FACING, facing), 2);

		checkAndPlaceAppropriately(world, pos);

	}

	private void checkAndPlaceAppropriately(World world, BlockPos pos)
	{
		TileEntityInventoryScanner connectedScanner = getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		EnumFacing facing = world.getBlockState(pos).get(FACING);
		int loopBoundary = facing == EnumFacing.WEST || facing == EnumFacing.EAST ? Math.abs(pos.getX() - connectedScanner.getPos().getX()) : (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getPos().getZ()) : 0);

		for(int i = 1; i < loopBoundary; i++)
		{
			if(world.getBlockState(pos.offset(facing, i)).getBlock() == SCContent.inventoryScannerField)
				return;
		}

		for(int i = 1; i < loopBoundary; i++)
		{
			world.setBlockState(pos.offset(facing, i), SCContent.inventoryScannerField.getDefaultState().with(FACING, facing));
		}

		CustomizableSCTE.link((CustomizableSCTE)world.getTileEntity(pos), connectedScanner);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if(world.isRemote)
			return;

		TileEntityInventoryScanner connectedScanner = null;
		for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
		{
			for(int i = 1; i <= ServerConfig.CONFIG.inventoryScannerRange.get(); i++)
			{
				BlockPos offsetIPos = pos.offset(facing, i);

				if(BlockUtils.getBlock(world, offsetIPos) == SCContent.inventoryScanner)
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
								if(field.get(BlockInventoryScannerField.FACING) == EnumFacing.WEST || field.get(BlockInventoryScannerField.FACING) == EnumFacing.EAST)
									world.destroyBlock(offsetJPos, false);
							}
							else if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
							{
								if(field.get(BlockInventoryScannerField.FACING) == EnumFacing.NORTH || field.get(BlockInventoryScannerField.FACING) == EnumFacing.SOUTH)
									world.destroyBlock(offsetJPos, false);
							}
						}
					}

					connectedScanner = (TileEntityInventoryScanner)world.getTileEntity(offsetIPos);
					break;
				}
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
		EnumFacing facing = world.getBlockState(pos).get(FACING);

		for(int i = 0; i <= ServerConfig.CONFIG.inventoryScannerRange.get(); i++)
		{
			BlockPos offsetPos = pos.offset(facing, i);
			Block block = BlockUtils.getBlock(world, offsetPos);
			IBlockState state = world.getBlockState(offsetPos);

			if(block != Blocks.AIR && block != SCContent.inventoryScannerField && block != SCContent.inventoryScanner)
				return null;

			if(block == SCContent.inventoryScanner && state.get(FACING) == facing.getOpposite())
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
	public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side)
	{
		if(!(blockAccess.getTileEntity(pos) instanceof TileEntityInventoryScanner) || ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getScanType() == null){
			SecurityCraft.log("type is null on the " + EffectiveSide.get() + " side");
			return 0;
		}

		return (((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getScanType().equals("redstone") && ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side)
	{
		if(((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getScanType() == null)
			return 0 ;

		return (((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getScanType().equals("redstone") && ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return new TileEntityInventoryScanner();
	}

}
