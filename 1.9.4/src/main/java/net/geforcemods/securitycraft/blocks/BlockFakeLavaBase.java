package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFakeLavaBase extends BlockStaticLiquid implements ITileEntityProvider, ICustomWailaDisplay {

	public BlockFakeLavaBase(Material p_i45429_1_){
		super(p_i45429_1_);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
	{
		if (!checkForMixing(worldIn, pos, state))
			updateLiquid(worldIn, pos, state);
	}

	private void updateLiquid(World worldIn, BlockPos p_176370_2_, IBlockState p_176370_3_)
	{
		BlockDynamicLiquid blockdynamicliquid = getFlowingBlock(blockMaterial);
		worldIn.setBlockState(p_176370_2_, blockdynamicliquid.getDefaultState().withProperty(LEVEL, p_176370_3_.getValue(LEVEL)), 2);
		worldIn.scheduleUpdate(p_176370_2_, blockdynamicliquid, tickRate(worldIn));
	}

	public static BlockDynamicLiquid getFlowingBlock(Material materialIn)
	{
		if (materialIn == Material.WATER)
			return (BlockDynamicLiquid) SCContent.bogusWaterFlowing;
		else if (materialIn == Material.LAVA)
			return (BlockDynamicLiquid) SCContent.bogusLavaFlowing;
		else
			throw new IllegalArgumentException("Invalid material");
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, IBlockState state, Entity par5Entity){
		if(!par1World.isRemote)
			if(par5Entity instanceof EntityPlayer){
				((EntityPlayer) par5Entity).heal(4);
				((EntityPlayer) par5Entity).extinguish();
			}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World par1World, BlockPos pos, IBlockState state){
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySCTE();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(Blocks.LAVA);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return false;
	}

}