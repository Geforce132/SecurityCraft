package net.geforcemods.securitycraft.blocks.mines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockMineBlock extends BaseFullMineBlock implements Fallable {
	public FallingBlockMineBlock(BlockBehaviour.Properties properties, Block disguisedBlock) {
		super(properties, disguisedBlock);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean flag) {
		level.scheduleTick(pos, this, 2);
	}

	@Override
	public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		tickAccess.scheduleTick(pos, this, 2);
		return super.updateShape(state, level, tickAccess, pos, facing, facingPos, facingState, random);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (FallingBlock.isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinY()) {
			BlockEntity be = level.getBlockEntity(pos);
			FallingBlockEntity entity = FallingBlockEntity.fall(level, pos, level.getBlockState(pos));

			entity.blockData = be.saveWithoutMetadata(level.registryAccess());
			level.addFreshEntity(entity);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if (rand.nextInt(16) == 0 && FallingBlock.isFree(level.getBlockState(pos.below()))) {
			double particleX = pos.getX() + rand.nextFloat();
			double particleY = pos.getY() - 0.05D;
			double particleZ = pos.getZ() + rand.nextFloat();

			level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), false, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
		}
	}
}
