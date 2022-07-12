package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedCryingObsidianBlock extends BaseReinforcedBlock {
	public ReinforcedCryingObsidianBlock(Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if (rand.nextInt(5) == 0) {
			Direction direction = Direction.getRandom(rand);

			if (direction != Direction.UP) {
				BlockPos offsetPos = pos.relative(direction);
				BlockState offsetState = level.getBlockState(offsetPos);

				if (!state.canOcclude() || !offsetState.isFaceSturdy(level, offsetPos, direction.getOpposite())) {
					double xOffset = direction.getStepX() == 0 ? rand.nextDouble() : 0.5D + direction.getStepX() * 0.6D;
					double yOffset = direction.getStepY() == 0 ? rand.nextDouble() : 0.5D + direction.getStepY() * 0.6D;
					double zOffset = direction.getStepZ() == 0 ? rand.nextDouble() : 0.5D + direction.getStepZ() * 0.6D;

					level.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}
}
