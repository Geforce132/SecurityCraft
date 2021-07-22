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

		registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		switch(rot)
		{
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch(state.getValue(AXIS))
				{
					case X:
						return state.setValue(AXIS, Direction.Axis.Z);
					case Z:
						return state.setValue(AXIS, Direction.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(AXIS);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(AXIS, vanillaState.getValue(RotatedPillarBlock.AXIS));
	}
}
