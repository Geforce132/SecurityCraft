package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedFallingBlock extends BaseReinforcedBlock implements Fallable {
	public ReinforcedFallingBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean flag) {
		level.scheduleTick(pos, this, 2);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		level.scheduleTick(currentPos, this, 2);
		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (FallingBlock.isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
			BlockEntity be = level.getBlockEntity(pos);
			FallingBlockEntity entity = FallingBlockEntity.fall(level, pos, level.getBlockState(pos));

			entity.blockData = be.saveWithoutMetadata(level.registryAccess());
			level.addFreshEntity(entity);
		}
	}
}