package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFakeLavaBase extends BlockStaticLiquid implements ITileEntityProvider, ICustomWailaDisplay {

	public BlockFakeLavaBase(Material material){
		super(material);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		if (!checkForMixing(world, pos, state))
			updateLiquid(world, pos, state);
	}

	private void updateLiquid(World world, BlockPos pos, IBlockState state)
	{
		BlockDynamicLiquid liquid = getFlowingBlock(blockMaterial);
		world.setBlockState(pos, liquid.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
		world.scheduleUpdate(pos, liquid, tickRate(world));
	}

	public static BlockDynamicLiquid getFlowingBlock(Material material)
	{
		if (material == Material.water)
			return (BlockDynamicLiquid) SCContent.bogusWaterFlowing;
		else if (material == Material.lava)
			return (BlockDynamicLiquid) SCContent.bogusLavaFlowing;
		else
			throw new IllegalArgumentException("Invalid material");
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity){
		if(!world.isRemote)
			if(entity instanceof EntityPlayer){
				((EntityPlayer) entity).heal(4);
				((EntityPlayer) entity).extinguish();
			}
	}
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos){
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySCTE();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(Blocks.lava);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return false;
	}

}