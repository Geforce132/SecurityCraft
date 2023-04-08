package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndRod;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedEndRodBlock extends BlockEndRod implements IReinforcedBlock {
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState oppositeState = world.getBlockState(pos.offset(facing.getOpposite()));

		if (oppositeState.getBlock() == this && oppositeState.getValue(FACING) == facing)
			return getDefaultState().withProperty(FACING, facing.getOpposite());

		return getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new OwnableBlockEntity();
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return Material.ROCK;
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.END_ROD);
	}

	@Override
	public int getAmount() {
		return 1;
	}
}
