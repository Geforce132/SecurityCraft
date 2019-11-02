package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;

public class BlockReinforcedRotatedPillar extends BlockReinforcedBase
{
	public static final EnumProperty<EnumFacing.Axis> AXIS = BlockStateProperties.AXIS;

	public BlockReinforcedRotatedPillar(Material mat, Block vB, String registryPath)
	{
		this(SoundType.STONE, mat, vB, registryPath);
	}

	public BlockReinforcedRotatedPillar(SoundType soundType, Material mat, Block vB, String registryPath)
	{
		super(soundType, mat, vB, registryPath);

		setDefaultState(stateContainer.getBaseState().with(AXIS, EnumFacing.Axis.Y));
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot)
	{
		switch(rot)
		{
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch(state.get(AXIS))
				{
					case X:
						return state.with(AXIS, EnumFacing.Axis.Z);
					case Z:
						return state.with(AXIS, EnumFacing.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
	{
		builder.add(AXIS);
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context)
	{
		return getDefaultState().with(AXIS, context.getFace().getAxis());
	}

	@Override
	public IBlockState getConvertedState(IBlockState vanillaState)
	{
		return getDefaultState().with(AXIS, vanillaState.get(BlockRotatedPillar.AXIS));
	}
}
