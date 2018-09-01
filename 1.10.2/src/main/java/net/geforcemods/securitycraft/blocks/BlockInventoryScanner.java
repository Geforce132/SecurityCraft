package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BlockInventoryScanner extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockInventoryScanner(Material par1Material) {
		super(par1Material);
		setSoundType(SoundType.STONE);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return true;
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

	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!worldIn.isRemote)
		{
			IBlockState block = worldIn.getBlockState(pos.north());
			IBlockState block1 = worldIn.getBlockState(pos.south());
			IBlockState block2 = worldIn.getBlockState(pos.west());
			IBlockState block3 = worldIn.getBlockState(pos.east());
			EnumFacing enumfacing = state.getValue(FACING);

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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote)
			return true;
		else{
			if(isFacingAnotherScanner(worldIn, pos))
				playerIn.openGui(SecurityCraft.instance, GuiHandler.INVENTORY_SCANNER_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("tile.securitycraft:inventoryScanner.name"), ClientUtils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);

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

		IBlockState block = par1World.getBlockState(pos.north());
		IBlockState block1 = par1World.getBlockState(pos.south());
		IBlockState block2 = par1World.getBlockState(pos.west());
		IBlockState block3 = par1World.getBlockState(pos.east());
		EnumFacing enumfacing = state.getValue(FACING);

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

		boolean place = true;

		if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.WEST)
		{
			for(int i = 1; i < Math.abs(pos.getX() - connectedScanner.getPos().getX()); i++)
			{
				if(par1World.getBlockState(pos.west(i)).getBlock() == SCContent.inventoryScannerField)
				{
					place = false;
					break;
				}
			}

			if(place)
			{
				for(int i = 1; i < Math.abs(pos.getX() - connectedScanner.getPos().getX()); i++)
				{
					par1World.setBlockState(pos.west(i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.WEST));
				}
			}
		}
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.EAST)
		{
			for(int i = 1; i < Math.abs(pos.getX() - connectedScanner.getPos().getX()); i++)
			{
				if(par1World.getBlockState(pos.east(i)).getBlock() == SCContent.inventoryScannerField)
				{
					place = false;
					break;
				}
			}

			if(place)
			{
				for(int i = 1; i < Math.abs(pos.getX() - connectedScanner.getPos().getX()); i++)
				{
					par1World.setBlockState(pos.east(i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.EAST));
				}
			}
		}
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH)
		{
			for(int i = 1; i < Math.abs(pos.getZ() - connectedScanner.getPos().getZ()); i++)
			{
				if(par1World.getBlockState(pos.north(i)).getBlock() == SCContent.inventoryScannerField)
				{
					place = false;
					break;
				}
			}

			if(place)
			{
				for(int i = 1; i < Math.abs(pos.getZ() - connectedScanner.getPos().getZ()); i++)
				{
					par1World.setBlockState(pos.north(i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.NORTH));
				}
			}
		}
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH)
		{
			for(int i = 1; i < Math.abs(pos.getZ() - connectedScanner.getPos().getZ()); i++)
			{
				if(par1World.getBlockState(pos.south(i)).getBlock() == SCContent.inventoryScannerField)
				{
					place = false;
					break;
				}
			}

			if(place)
			{
				for(int i = 1; i < Math.abs(pos.getZ() - connectedScanner.getPos().getZ()); i++)
				{
					par1World.setBlockState(pos.south(i), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.SOUTH));
				}
			}
		}

		if(place)
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
					IBlockState field = par1World.getBlockState(pos.west(j));

					if(field.getBlock() == SCContent.inventoryScannerField && (field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.EAST || field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.WEST))
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
					IBlockState field = par1World.getBlockState(pos.east(j));

					if(field.getBlock() == SCContent.inventoryScannerField && (field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.EAST || field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.WEST))
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
					IBlockState field = par1World.getBlockState(pos.north(j));

					if(field.getBlock() == SCContent.inventoryScannerField && (field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.NORTH || field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.SOUTH))
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
					IBlockState field = par1World.getBlockState(pos.south(j));

					if(field.getBlock() == SCContent.inventoryScannerField && (field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.NORTH || field.getValue(BlockInventoryScannerField.FACING) == EnumFacing.SOUTH))
						par1World.destroyBlock(pos.south(j), false);
				}

				connectedScanner = (TileEntityInventoryScanner)par1World.getTileEntity(pos.south(i));
				break;
			}
		}

		for(int i = 10; i < ((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents().length; i++)
		{
			if(((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents()[i] != null)
				par1World.spawnEntity(new EntityItem(par1World, pos.getX(), pos.getY(), pos.getZ(), ((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents()[i]));
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
		switch(world.getBlockState(pos).getValue(FACING))
		{
			case WEST:
				for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
				{
					Block b = BlockUtils.getBlock(world, pos.west(i));

					if(b != Blocks.AIR && b != SCContent.inventoryScannerField && b != SCContent.inventoryScanner)
						return null;

					if(b == SCContent.inventoryScanner && world.getBlockState(pos.west(i)).getValue(FACING) == EnumFacing.EAST)
						return (TileEntityInventoryScanner)world.getTileEntity(pos.west(i));
				}

				return null;
			case EAST:
				for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
				{
					Block b = BlockUtils.getBlock(world, pos.east(i));

					if(b != Blocks.AIR && b != SCContent.inventoryScannerField && b != SCContent.inventoryScanner)
						return null;

					if(b == SCContent.inventoryScanner && world.getBlockState(pos.east(i)).getValue(FACING) == EnumFacing.WEST)
						return (TileEntityInventoryScanner)world.getTileEntity(pos.east(i));
				}

				return null;
			case NORTH:
				for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
				{
					Block b = BlockUtils.getBlock(world, pos.north(i));

					if(b != Blocks.AIR && b != SCContent.inventoryScannerField && b != SCContent.inventoryScanner)
						return null;

					if(b == SCContent.inventoryScanner && world.getBlockState(pos.north(i)).getValue(FACING) == EnumFacing.SOUTH)
						return (TileEntityInventoryScanner)world.getTileEntity(pos.north(i));
				}

				return null;
			case SOUTH:
				for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
				{
					Block b = BlockUtils.getBlock(world, pos.south(i));

					if(b != Blocks.AIR && b != SCContent.inventoryScannerField && b != SCContent.inventoryScanner)
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
		if(!(blockAccess.getTileEntity(pos) instanceof TileEntityInventoryScanner) || ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getType() == null){
			SecurityCraft.log("type is null on the " + FMLCommonHandler.instance().getEffectiveSide() + " side");
			return 0 ;
		}

		return (((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getType().matches("redstone") && ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		if(((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getType() == null)
			return 0 ;

		return (((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getType().matches("redstone") && ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
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
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityInventoryScanner();
	}

}
