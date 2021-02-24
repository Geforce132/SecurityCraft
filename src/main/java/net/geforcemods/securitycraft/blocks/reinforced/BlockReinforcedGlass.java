package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedGlass extends BlockGlass implements ITileEntityProvider, IReinforcedBlock {

	public BlockReinforcedGlass(Material material) {
		super(material, false);
		setSoundType(SoundType.GLASS);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.GLASS);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		IBlockState offsetState = blockAccess.getBlockState(pos.offset(side));
		Block offsetBlock = offsetState.getBlock();

		if(this == SCContent.reinforcedGlass)
		{
			if(blockState != offsetState)
				return true;

			if(offsetBlock == this)
				return false;
		}

		return true;
	}
}
