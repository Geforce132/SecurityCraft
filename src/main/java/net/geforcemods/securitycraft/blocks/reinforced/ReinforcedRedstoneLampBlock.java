package net.geforcemods.securitycraft.blocks.reinforced;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;

public class ReinforcedRedstoneLampBlock extends BaseReinforcedBlock {
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public ReinforcedRedstoneLampBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties, vB);

		registerDefaultState(defaultBlockState().setValue(LIT, false));
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(LIT, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean isMoving) {
		if (!level.isClientSide()) {
			boolean isLit = state.getValue(LIT);

			if (isLit != level.hasNeighborSignal(pos)) {
				if (isLit)
					level.scheduleTick(pos, this, 4);
				else
					level.setBlock(pos, state.cycle(LIT), 2);
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		if (state.getValue(LIT) && !level.hasNeighborSignal(pos))
			level.setBlock(pos, state.cycle(LIT), 2);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}
}
