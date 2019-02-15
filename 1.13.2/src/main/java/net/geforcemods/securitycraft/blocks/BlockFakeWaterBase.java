package net.geforcemods.securitycraft.blocks;

import javafx.geometry.Side;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
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
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFakeWaterBase extends BlockStaticLiquid implements ITileEntityProvider, ICustomWailaDisplay {

	public BlockFakeWaterBase(Material material)
	{
		super(material);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (!checkForMixing(world, pos, state))
			updateLiquid(world, pos, state);
	}

	private void updateLiquid(World world, BlockPos pos, IBlockState state)
	{
		BlockDynamicLiquid liquid = getFlowingBlock(this.material);
		world.setBlockState(pos, liquid.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
		world.scheduleUpdate(pos, liquid, tickRate(world));
	}

	public static BlockDynamicLiquid getFlowingBlock(Material material)
	{
		if (material == Material.WATER)
			return (BlockDynamicLiquid) SCContent.bogusWaterFlowing;
		else if (material == Material.LAVA)
			return (BlockDynamicLiquid) SCContent.bogusLavaFlowing;
		else
			throw new IllegalArgumentException("Invalid material");
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if(!world.isRemote)
			if(entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
				((EntityPlayer) entity).attackEntityFrom(CustomDamageSources.fakeWater, 5F);
			else
				entity.attackEntityFrom(CustomDamageSources.fakeWater, 5F);
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySCTE();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(Blocks.WATER);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return false;
	}
}
