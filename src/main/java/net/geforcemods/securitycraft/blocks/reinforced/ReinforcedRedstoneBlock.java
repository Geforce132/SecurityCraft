package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedRedstoneBlock extends BaseReinforcedBlock {
	public ReinforcedRedstoneBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return 15;
	}

	public static class DoorActivator implements IDoorActivator {
		private final List<Block> blocks = Arrays.asList(SCContent.REINFORCED_REDSTONE_BLOCK.get());

		@Override
		public boolean isPowering(Level level, BlockPos pos, BlockState state, BlockEntity be, Direction direction, int distance) {
			return distance == 1;
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
