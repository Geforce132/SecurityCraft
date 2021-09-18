package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.ITileEntityProvider;
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

public class BlockReinforcedPistonExtension extends BlockPistonExtension implements IReinforcedBlock, ITileEntityProvider {

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));

		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			BlockPos behindPos = pos.offset(state.getValue(FACING).getOpposite());
			Block behindBlock = world.getBlockState(behindPos).getBlock();

			if (behindBlock == SCContent.reinforcedPiston || behindBlock == SCContent.reinforcedStickyPiston) {
				world.setBlockToAir(behindPos);
			}
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);

		EnumFacing behind = state.getValue(FACING).getOpposite();
		pos = pos.offset(behind);
		IBlockState behindState = world.getBlockState(pos);

		if ((behindState.getBlock() == SCContent.reinforcedPiston || behindState.getBlock() == SCContent.reinforcedStickyPiston) && behindState.getValue(BlockPistonBase.EXTENDED)) {
			behindState.getBlock().dropBlockAsItem(world, pos, behindState, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		EnumFacing facing = state.getValue(FACING);
		BlockPos behindPos = pos.offset(facing.getOpposite());
		IBlockState behindState = world.getBlockState(behindPos);

		if (behindState.getBlock() != SCContent.reinforcedPiston && behindState.getBlock() != SCContent.reinforcedStickyPiston) {
			world.setBlockToAir(pos);
		} else {
			behindState.neighborChanged(world, behindPos, block, fromPos);
		}
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return new ItemStack(state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY ? SCContent.reinforcedStickyPiston : SCContent.reinforcedPiston);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.PISTON_HEAD);
	}

	@Override
	public int getAmount() {
		return 1;
	}
}
