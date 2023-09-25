package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ReinforcedRedstoneLampBlock extends BaseReinforcedBlock {
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public ReinforcedRedstoneLampBlock(AbstractBlock.Properties properties, Block vB) {
		super(properties, vB);

		registerDefaultState(defaultBlockState().setValue(LIT, false));
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return defaultBlockState().setValue(LIT, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!level.isClientSide) {
			boolean isLit = state.getValue(LIT);

			if (isLit != level.hasNeighborSignal(pos)) {
				if (isLit)
					level.getBlockTicks().scheduleTick(pos, this, 4);
				else
					level.setBlock(pos, state.cycle(LIT), 2);
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random rand) {
		if (state.getValue(LIT) && !level.hasNeighborSignal(pos))
			level.setBlock(pos, state.cycle(LIT), 2);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}
}
