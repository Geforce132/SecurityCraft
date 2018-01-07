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
import net.minecraft.entity.item.EntityItem;
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

	public BlockInventoryScanner(Material par1Material) {
		super(par1Material);
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(par1World, pos, state);
		setDefaultFacing(par1World, pos, state);
	}

	/**
	 * set a blocks direction
	 */
	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!worldIn.isRemote)
		{
			Block block = worldIn.getBlockState(pos.north()).getBlock();
			Block block1 = worldIn.getBlockState(pos.south()).getBlock();
			Block block2 = worldIn.getBlockState(pos.west()).getBlock();
			Block block3 = worldIn.getBlockState(pos.east()).getBlock();
			EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
				enumfacing = EnumFacing.SOUTH;
			else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
				enumfacing = EnumFacing.NORTH;
			else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
				enumfacing = EnumFacing.EAST;
			else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
				enumfacing = EnumFacing.WEST;

			worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

	@Override
	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(par1World.isRemote)
			return true;
		else{
			if(isFacingAnotherScanner(par1World, pos))
				par5EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.INVENTORY_SCANNER_GUI_ID, par1World, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.inventoryScanner.name"), StatCollector.translateToLocal("messages.invScan.notConnected"), EnumChatFormatting.RED);

			return true;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		if(par1World.isRemote)
			return;

		Block block = par1World.getBlockState(pos.north()).getBlock();
		Block block1 = par1World.getBlockState(pos.south()).getBlock();
		Block block2 = par1World.getBlockState(pos.west()).getBlock();
		Block block3 = par1World.getBlockState(pos.east()).getBlock();
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

		if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
			enumfacing = EnumFacing.SOUTH;
		else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
			enumfacing = EnumFacing.NORTH;
		else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
			enumfacing = EnumFacing.EAST;
		else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
			enumfacing = EnumFacing.WEST;

		par1World.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);

		checkAndPlaceAppropriately(par1World, pos);

	}

	private void checkAndPlaceAppropriately(World par1World, BlockPos pos)
	{
		TileEntityInventoryScanner connectedScanner = getConnectedInventoryScanner(par1World, pos);

		if(connectedScanner == null)
			return;

		if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.WEST)
		{
			for(int i = 1; i < Math.abs(pos.getX() - connectedScanner.getPos().getX()); i++)
			{
				par1World.setBlockState(pos.west(i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.WEST));
			}
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.EAST)
		{
			for(int i = 1; i < Math.abs(pos.getX() - connectedScanner.getPos().getX()); i++)
			{
				par1World.setBlockState(pos.east(i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.EAST));
			}
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH)
		{
			for(int i = 1; i < Math.abs(pos.getZ() - connectedScanner.getPos().getZ()); i++)
			{
				par1World.setBlockState(pos.north(i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.NORTH));
			}
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH)
		{
			for(int i = 1; i < Math.abs(pos.getZ() - connectedScanner.getPos().getZ()); i++)
			{
				par1World.setBlockState(pos.south(i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.SOUTH));
			}
		}

		CustomizableSCTE.link((CustomizableSCTE)par1World.getTileEntity(pos), connectedScanner);
	}

	@Override
	public void breakBlock(World par1World, BlockPos pos, IBlockState state)
	{
		if(par1World.isRemote)
			return;

		TileEntityInventoryScanner connectedScanner = null;

		for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			if(BlockUtils.getBlock(par1World, pos.west(i)) == SCContent.inventoryScanner)
			{
				for(int j = 1; j < i; j++)
				{
					if(BlockUtils.getBlock(par1World, pos.west(j)) == SCContent.inventoryScannerField)
						par1World.destroyBlock(pos.west(j), false);
				}

				connectedScanner = (TileEntityInventoryScanner)par1World.getTileEntity(pos.west(i));
				break;
			}
		}

		for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			if(BlockUtils.getBlock(par1World, pos.east(i)) == SCContent.inventoryScanner)
			{
				for(int j = 1; j < i; j++)
				{
					if(BlockUtils.getBlock(par1World, pos.east(j)) == SCContent.inventoryScannerField)
						par1World.destroyBlock(pos.east(j), false);
				}

				connectedScanner = (TileEntityInventoryScanner)par1World.getTileEntity(pos.east(i));
				break;
			}
		}

		for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			if(BlockUtils.getBlock(par1World, pos.north(i)) == SCContent.inventoryScanner)
			{
				for(int j = 1; j < i; j++)
				{
					if(BlockUtils.getBlock(par1World, pos.north(j)) == SCContent.inventoryScannerField)
						par1World.destroyBlock(pos.north(j), false);
				}

				connectedScanner = (TileEntityInventoryScanner)par1World.getTileEntity(pos.north(i));
				break;
			}
		}

		for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			if(BlockUtils.getBlock(par1World, pos.south(i)) == SCContent.inventoryScanner)
			{
				for(int j = 1; j < i; j++)
				{
					if(BlockUtils.getBlock(par1World, pos.south(j)) == SCContent.inventoryScannerField)
						par1World.destroyBlock(pos.south(j), false);
				}

				connectedScanner = (TileEntityInventoryScanner)par1World.getTileEntity(pos.south(i));
				break;
			}
		}

		for(int i = 0; i < ((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents().length; i++)
		{
			if(((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents()[i] != null)
				par1World.spawnEntityInWorld(new EntityItem(par1World, pos.getX(), pos.getY(), pos.getZ(), ((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents()[i]));
		}

		if(connectedScanner != null)
		{
			for(int i = 0; i < connectedScanner.getContents().length; i++)
			{
				connectedScanner.getContents()[i] = null;
			}
		}

		super.breakBlock(par1World, pos, state);
	}

	private boolean isFacingAnotherScanner(World world, BlockPos pos)
	{
		return getConnectedInventoryScanner(world, pos) != null;
	}

	public static TileEntityInventoryScanner getConnectedInventoryScanner(World world, BlockPos pos)
	{
		switch((EnumFacing) world.getBlockState(pos).getValue(FACING))
		{
			case WEST:
				for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
				{
					Block b = BlockUtils.getBlock(world, pos.west(i));

					if(b != Blocks.air && b != SCContent.inventoryScannerField && b != SCContent.inventoryScanner)
						return null;

					if(b == SCContent.inventoryScanner && world.getBlockState(pos.west(i)).getValue(FACING) == EnumFacing.EAST)
						return (TileEntityInventoryScanner)world.getTileEntity(pos.west(i));
				}

				return null;
			case EAST:
				for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
				{
					Block b = BlockUtils.getBlock(world, pos.east(i));

					if(b != Blocks.air && b != SCContent.inventoryScannerField && b != SCContent.inventoryScanner)
						return null;

					if(b == SCContent.inventoryScanner && world.getBlockState(pos.east(i)).getValue(FACING) == EnumFacing.WEST)
						return (TileEntityInventoryScanner)world.getTileEntity(pos.east(i));
				}

				return null;
			case NORTH:
				for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
				{
					Block b = BlockUtils.getBlock(world, pos.north(i));

					if(b != Blocks.air && b != SCContent.inventoryScannerField && b != SCContent.inventoryScanner)
						return null;

					if(b == SCContent.inventoryScanner && world.getBlockState(pos.north(i)).getValue(FACING) == EnumFacing.SOUTH)
						return (TileEntityInventoryScanner)world.getTileEntity(pos.north(i));
				}

				return null;
			case SOUTH:
				for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
				{
					Block b = BlockUtils.getBlock(world, pos.south(i));

					if(b != Blocks.air && b != SCContent.inventoryScannerField && b != SCContent.inventoryScanner)
						return null;

					if(b == SCContent.inventoryScanner && world.getBlockState(pos.south(i)).getValue(FACING) == EnumFacing.NORTH)
						return (TileEntityInventoryScanner)world.getTileEntity(pos.south(i));
				}

				return null;
			default: return null;
		}
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
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side)
	{
		if(!(par1IBlockAccess.getTileEntity(pos) instanceof TileEntityInventoryScanner) || ((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).getType() == null){
			SecurityCraft.log("type is null on the " + FMLCommonHandler.instance().getEffectiveSide() + " side");
			return 0 ;
		}

		return (((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side)
	{
		if(((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).getType() == null)
			return 0 ;

		return (((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
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
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityInventoryScanner();
	}

}
