package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryScanner extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockInventoryScanner(Material material) {
		super(material);
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return true;
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

	/**
	 * set a blocks direction
	 */
	private void setDefaultFacing(World world, BlockPos pos, IBlockState state)
	{
		if (!world.isRemote)
		{
			Block north = world.getBlockState(pos.north()).getBlock();
			Block south = world.getBlockState(pos.south()).getBlock();
			Block west = world.getBlockState(pos.west()).getBlock();
			Block east = world.getBlockState(pos.east()).getBlock();
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else{
			if(isFacingAnotherScanner(world, pos))
				player.openGui(SecurityCraft.instance, GuiHandler.INVENTORY_SCANNER_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securitycraft:inventoryScanner.name"), StatCollector.translateToLocal("messages.securitycraft:invScan.notConnected"), EnumChatFormatting.RED);

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

		Block north = world.getBlockState(pos.north()).getBlock();
		Block south = world.getBlockState(pos.south()).getBlock();
		Block west = world.getBlockState(pos.west()).getBlock();
		Block east = world.getBlockState(pos.east()).getBlock();
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

		checkAndPlaceAppropriately(world, pos);

	}

	private void checkAndPlaceAppropriately(World world, BlockPos pos)
	{
		TileEntityInventoryScanner connectedScanner = getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
		int loopBoundary = facing == EnumFacing.WEST || facing == EnumFacing.EAST ? Math.abs(pos.getX() - connectedScanner.getPos().getX()) : (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getPos().getZ()) : 0);

		for(int i = 1; i < loopBoundary; i++)
		{
			if(world.getBlockState(pos.offset(facing, i)).getBlock() == SCContent.inventoryScannerField)
				return;
		}

		for(int i = 1; i < loopBoundary; i++)
		{
			world.setBlockState(pos.offset(facing, i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, facing));
		}

		CustomizableSCTE.link((CustomizableSCTE)world.getTileEntity(pos), connectedScanner);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if(world.isRemote)
			return;

		TileEntityInventoryScanner connectedScanner = null;

		for(EnumFacing facing : EnumFacing.HORIZONTALS)
		{
			for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
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

		if(connectedScanner != null)
		{
			for(int i = 0; i < connectedScanner.getContents().length; i++)
			{
				connectedScanner.getContents()[i] = null;
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

		for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			BlockPos offsetPos = pos.offset(facing, i);
			Block block = BlockUtils.getBlock(world, offsetPos);
			IBlockState state = world.getBlockState(offsetPos);

			if(block != Blocks.air && block != SCContent.inventoryScannerField && block != SCContent.inventoryScanner)
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
	public boolean canProvidePower()
	{
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
	{
		if(!(world.getTileEntity(pos) instanceof TileEntityInventoryScanner) || ((TileEntityInventoryScanner) world.getTileEntity(pos)).getType() == null){
			SecurityCraft.log("type is null on the " + FMLCommonHandler.instance().getEffectiveSide() + " side");
			return 0 ;
		}

		return (((TileEntityInventoryScanner) world.getTileEntity(pos)).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
	{
		if(((TileEntityInventoryScanner) world.getTileEntity(pos)).getType() == null)
			return 0 ;

		return (((TileEntityInventoryScanner) world.getTileEntity(pos)).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
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
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityInventoryScanner();
	}

}
