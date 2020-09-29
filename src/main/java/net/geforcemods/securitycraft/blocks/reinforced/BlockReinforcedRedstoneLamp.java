package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedRedstoneLamp extends BlockReinforcedBase
{
	public static final PropertyBool LIT = PropertyBool.create("lit");

	public BlockReinforcedRedstoneLamp()
	{
		super(Material.REDSTONE_LIGHT, 1, Blocks.REDSTONE_LAMP, Blocks.LIT_REDSTONE_LAMP);

		setDefaultState(getDefaultState().withProperty(LIT, false));
		setSoundType(SoundType.GLASS);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if(!world.isRemote)
		{
			boolean isOn = state.getValue(LIT);

			if(isOn && !world.isBlockPowered(pos))
				world.setBlockState(pos, getDefaultState().withProperty(LIT, false), 2);
			else if(!isOn && world.isBlockPowered(pos))
				world.setBlockState(pos, getDefaultState().withProperty(LIT, true), 2);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if(!world.isRemote)
		{
			boolean isOn = state.getValue(LIT);

			if(isOn && !world.isBlockPowered(pos))
				world.scheduleUpdate(pos, this, 4);
			else if(!isOn && world.isBlockPowered(pos))
				world.setBlockState(pos, getDefaultState().withProperty(LIT, true), 2);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(!world.isRemote)
		{
			if(state.getValue(LIT) && !world.isBlockPowered(pos))
				world.setBlockState(pos, getDefaultState().withProperty(LIT, false), 2);
		}
	}

	@Override
	public int getLightValue(IBlockState state)
	{
		return state.getValue(LIT) ? 15 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(LIT, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(LIT) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, LIT);
	}
}
