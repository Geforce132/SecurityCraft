package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.items.ItemKeycardBase;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKeycardReader extends BlockOwnable  {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockKeycardReader(Material par2Material) {
		super(par2Material);
		setSoundType(SoundType.METAL);
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
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);

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
	}

	public void insertCard(World par1World, BlockPos pos, ItemStack par5ItemStack, EntityPlayer par6EntityPlayer) {
		if(ModuleUtils.checkForModule(par1World, pos, par6EntityPlayer, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(par1World, pos, par6EntityPlayer, EnumCustomModules.BLACKLIST))
			return;

		int securityLevel = 0;

		if(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassword() != null)
			securityLevel = Integer.parseInt(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassword());

		if((!((TileEntityKeycardReader)par1World.getTileEntity(pos)).doesRequireExactKeycard() && securityLevel <= ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack) || ((TileEntityKeycardReader)par1World.getTileEntity(pos)).doesRequireExactKeycard() && securityLevel == ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack))){
			if(((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack) == 6 && par5ItemStack.getTagCompound() != null && !par6EntityPlayer.capabilities.isCreativeMode){
				par5ItemStack.getTagCompound().setInteger("Uses", par5ItemStack.getTagCompound().getInteger("Uses") - 1);

				if(par5ItemStack.getTagCompound().getInteger("Uses") <= 0)
					par5ItemStack.stackSize--;
			}

			BlockKeycardReader.activate(par1World, pos);
		}
		else if(Integer.parseInt(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassword()) != 0)
			PlayerUtils.sendMessageToPlayer(par6EntityPlayer, ClientUtils.localize("tile.securitycraft:keycardReader.name"), ClientUtils.localize("messages.securitycraft:keycardReader.required").replace("#r", ((IPasswordProtected) par1World.getTileEntity(pos)).getPassword()).replace("#c", "" + ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack)), TextFormatting.RED);
		else
			PlayerUtils.sendMessageToPlayer(par6EntityPlayer, ClientUtils.localize("tile.securitycraft:keycardReader.name"), ClientUtils.localize("messages.securitycraft:keycardReader.notSet"), TextFormatting.RED);

	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote)
			return true;

		if(playerIn.inventory.getCurrentItem() == null || (!(playerIn.inventory.getCurrentItem().getItem() instanceof ItemKeycardBase) && playerIn.inventory.getCurrentItem().getItem() != SCContent.adminTool))
			((TileEntityKeycardReader) worldIn.getTileEntity(pos)).openPasswordGUI(playerIn);
		else if(playerIn.inventory.getCurrentItem().getItem() == SCContent.adminTool)
			((BlockKeycardReader) BlockUtils.getBlock(worldIn, pos)).insertCard(worldIn, pos, new ItemStack(SCContent.limitedUseKeycard, 1), playerIn);
		else
			((BlockKeycardReader) BlockUtils.getBlock(worldIn, pos)).insertCard(worldIn, pos, playerIn.inventory.getCurrentItem(), playerIn);

		return false;
	}

	public static void activate(World par1World, BlockPos pos){
		BlockUtils.setBlockProperty(par1World, pos, POWERED, true);
		par1World.notifyNeighborsOfStateChange(pos, SCContent.keycardReader);
		par1World.scheduleUpdate(pos, SCContent.keycardReader, 60);
	}

	@Override
	public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
		if(!par1World.isRemote){
			BlockUtils.setBlockProperty(par1World, pos, POWERED, false);
			par1World.notifyNeighborsOfStateChange(pos, SCContent.keycardReader);
		}
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand){
		if((stateIn.getValue(POWERED))){
			double d0 = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double d1 = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double d2 = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;


			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		if((blockState.getValue(POWERED)))
			return 15;
		else
			return 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if(meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(state.getValue(POWERED).booleanValue())
			return (state.getValue(FACING).getIndex() + 6);
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING, POWERED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityKeycardReader();
	}

}
