package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedBase extends BlockOwnable implements IReinforcedBlock
{
	private List<Block> vanillaBlocks;
	private int amount;

	public BlockReinforcedBase(Material mat, int a, Block... vB)
	{
		super(mat);

		vanillaBlocks = Arrays.asList(vB);
		amount = a;
	}

	public BlockReinforcedBase(Material mat, int a, SoundType sound, Block... vB)
	{
		super(mat);

		setSoundType(sound);
		vanillaBlocks = Arrays.asList(vB);
		amount = a;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return this == SCContent.reinforcedIce ? false : super.isOpaqueCube(state);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		if(this == SCContent.reinforcedIce)
			return world.getBlockState(pos.offset(side)).getBlock() != this;
		else return super.shouldSideBeRendered(state, world, pos, side);
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return this == SCContent.reinforcedIce ? BlockRenderLayer.TRANSLUCENT : super.getRenderLayer();
	}

	@Override
	public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
	{
		return this == SCContent.reinforcedIce || this == SCContent.reinforcedPackedIce ? 0.98F : super.getSlipperiness(state, world, pos, entity);
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
	{
		return this == SCContent.reinforcedNetherrack && side == EnumFacing.UP;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return vanillaBlocks;
	}

	@Override
	public int getAmount()
	{
		return amount;
	}
}
