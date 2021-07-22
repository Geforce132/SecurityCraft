package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(AXIS);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(AXIS, vanillaState.getValue(RotatedPillarBlock.AXIS));
	}
}
