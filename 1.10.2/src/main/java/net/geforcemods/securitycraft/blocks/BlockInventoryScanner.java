package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
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
			if(isFacingAnotherBlock(worldIn, pos))
				playerIn.openGui(SecurityCraft.instance, GuiHandler.INVENTORY_SCANNER_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("tile.inventoryScanner.name"), ClientUtils.localize("messages.invScan.notConnected"), TextFormatting.RED);

			return true;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
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

	private void checkAndPlaceAppropriately(World par1World, BlockPos pos) {
		if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west(2)) == this && BlockUtils.getBlock(par1World, pos.west()) == Blocks.AIR && par1World.getBlockState(pos.west(2)).getValue(FACING) == EnumFacing.EAST)
			par1World.setBlockState(pos.west(), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.WEST));
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east(2)) == this && BlockUtils.getBlock(par1World, pos.east()) == Blocks.AIR && par1World.getBlockState(pos.east(2)).getValue(FACING) == EnumFacing.WEST)
			par1World.setBlockState(pos.east(), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.EAST));
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north(2)) == this && BlockUtils.getBlock(par1World, pos.north()) == Blocks.AIR && par1World.getBlockState(pos.north(2)).getValue(FACING) == EnumFacing.SOUTH)
			par1World.setBlockState(pos.north(), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.NORTH));
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south(2)) == this && BlockUtils.getBlock(par1World, pos.south()) == Blocks.AIR && par1World.getBlockState(pos.south(2)).getValue(FACING) == EnumFacing.NORTH)
			par1World.setBlockState(pos.south(), SCContent.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.SOUTH));
	}

	private boolean isFacingAnotherBlock(World par1World, BlockPos pos){
		if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west(2)) == this && BlockUtils.getBlock(par1World, pos.west()) == SCContent.inventoryScannerField && par1World.getBlockState(pos.west(2)).getValue(FACING) == EnumFacing.EAST)
			return true;
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east(2)) == this && BlockUtils.getBlock(par1World, pos.east()) == SCContent.inventoryScannerField && par1World.getBlockState(pos.east(2)).getValue(FACING) == EnumFacing.WEST)
			return true;
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north(2)) == this && BlockUtils.getBlock(par1World, pos.north()) == SCContent.inventoryScannerField && par1World.getBlockState(pos.north(2)).getValue(FACING) == EnumFacing.SOUTH)
			return true;
		else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south(2)) == this && BlockUtils.getBlock(par1World, pos.south()) == SCContent.inventoryScannerField && par1World.getBlockState(pos.south(2)).getValue(FACING) == EnumFacing.NORTH)
			return true;
		else
			return false;
	}

	@Override
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
		for(int i = 0; i < ((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents().length; i++)
			if(((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents()[i] != null){
				EntityItem item = new EntityItem(par1World, pos.getX(), pos.getY(), pos.getZ(), ((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents()[i]);
				WorldUtils.addScheduledTask(par1World, () -> par1World.spawnEntityInWorld(item));
			}

		if(state.getValue(FACING) == EnumFacing.NORTH && par1World.getBlockState(pos.south()).getBlock() == SCContent.inventoryScannerField)
			BlockUtils.destroyBlock(par1World, pos.south(), false);
		else if(state.getValue(FACING) == EnumFacing.SOUTH && par1World.getBlockState(pos.north()).getBlock() == SCContent.inventoryScannerField)
			BlockUtils.destroyBlock(par1World, pos.north(), false);
		else if(state.getValue(FACING) == EnumFacing.EAST && par1World.getBlockState(pos.west()).getBlock() == SCContent.inventoryScannerField)
			BlockUtils.destroyBlock(par1World, pos.west(), false);
		else if(state.getValue(FACING) == EnumFacing.WEST && par1World.getBlockState(pos.east()).getBlock() == SCContent.inventoryScannerField)
			BlockUtils.destroyBlock(par1World, pos.east(), false);

		if(state.getValue(FACING) == EnumFacing.NORTH && par1World.getBlockState(pos.south(2)).getBlock() == SCContent.inventoryScanner && par1World.getBlockState(pos.south(2)).getValue(FACING) == EnumFacing.SOUTH)
			ModuleUtils.insertModule(par1World, pos.south(2), null);
		else if(state.getValue(FACING) == EnumFacing.SOUTH && par1World.getBlockState(pos.north(2)).getBlock() == SCContent.inventoryScanner && par1World.getBlockState(pos.north(2)).getValue(FACING) == EnumFacing.NORTH)
			ModuleUtils.insertModule(par1World, pos.north(2), null);
		else if(state.getValue(FACING) == EnumFacing.EAST && par1World.getBlockState(pos.west(2)).getBlock() == SCContent.inventoryScanner && par1World.getBlockState(pos.west(2)).getValue(FACING) == EnumFacing.WEST)
			ModuleUtils.insertModule(par1World, pos.west(2), null);
		else if(state.getValue(FACING) == EnumFacing.WEST && par1World.getBlockState(pos.east(2)).getBlock() == SCContent.inventoryScanner && par1World.getBlockState(pos.east(2)).getValue(FACING) == EnumFacing.EAST)
			ModuleUtils.insertModule(par1World, pos.east(2), null);

		par1World.removeTileEntity(pos);

		super.breakBlock(par1World, pos, state);
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
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	/* TODO: no clue about this
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }*/

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
