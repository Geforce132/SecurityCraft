package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

public class ReinforcedRotatedPillarBlock extends BaseReinforcedBlock
{
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

	public ReinforcedRotatedPillarBlock(Block.Properties properties, Block vB)
	{
		this(properties, () -> vB);
	}

	public ReinforcedRotatedPillarBlock(Block.Properties properties, Supplier<Block> vB)
	{
		super(properties, vB);

		setDefaultState(stateContainer.getBaseState().with(AXIS, Direction.Axis.Y));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		switch(rot)
		{
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch(state.get(AXIS))
				{
					case X:
						return state.with(AXIS, Direction.Axis.Z);
					case Z:
						return state.with(AXIS, Direction.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(AXIS);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return getDefaultState().with(AXIS, context.getFace().getAxis());
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(AXIS, vanillaState.get(RotatedPillarBlock.AXIS));
	}
}
