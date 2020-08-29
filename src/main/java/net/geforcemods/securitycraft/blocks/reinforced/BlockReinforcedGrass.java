package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedGrass extends BlockReinforcedBase implements IGrowable
{
	public static final PropertyBool SNOWY = PropertyBool.create("snowy");

	public BlockReinforcedGrass()
	{
		super(Material.GRASS, 1, Blocks.GRASS);

		setDefaultState(blockState.getBaseState().withProperty(SNOWY, false));
		setTickRandomly(true);
		setSoundType(SoundType.PLANT);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		Block block = world.getBlockState(pos.up()).getBlock();

		return state.withProperty(SNOWY, block == Blocks.SNOW || block == Blocks.SNOW_LAYER || block == SCContent.reinforcedSnowBlock);
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
	{
		return true;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
	{
		return true;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state)
	{
		BlockPos posAbove = pos.up();

		for(int i = 0; i < 128; ++i)
		{
			BlockPos tempPos = posAbove;
			int j = 0;

			while(true)
			{
				if(j >= i / 16)
				{
					if(world.isAirBlock(tempPos))
					{
						if(rand.nextInt(8) == 0)
							world.getBiome(tempPos).plantFlower(world, rand, tempPos);
						else
						{
							IBlockState grass = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

							if(Blocks.TALLGRASS.canBlockStay(world, tempPos, grass))
								world.setBlockState(tempPos, grass, 3);
						}
					}

					break;
				}

				tempPos = tempPos.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

				if(world.getBlockState(tempPos.down()).getBlock() != this || world.getBlockState(tempPos).isNormalCube())
					break;

				++j;
			}
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, SNOWY);
	}
}
