package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedObserver extends BlockObserver implements IReinforcedBlock
{
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return side == state.getValue(BlockObserver.FACING);
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.OBSERVER);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityOwnable();
	}
}
