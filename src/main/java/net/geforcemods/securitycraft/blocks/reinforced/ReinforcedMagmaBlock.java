package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.enums.BubbleColumnDirection;
import net.neoforged.neoforge.common.util.TriState;

public class ReinforcedMagmaBlock extends BaseReinforcedBlock {
	public ReinforcedMagmaBlock(Block vanillaBlock) {
		super(vanillaBlock);
	}

	@Override
	public BubbleColumnDirection getBubbleColumnDirection(BlockState state) {
		return BubbleColumnDirection.DOWNWARD;
	}

	@Override
	public TriState canSustainPlant(BlockState soilState, BlockGetter level, BlockPos soilPos, Direction facing, BlockState plant) {
		Block plantable = plant.getBlock();

		if (plantable instanceof SeagrassBlock || plantable instanceof TallSeagrassBlock)
			return TriState.FALSE;

		return super.canSustainPlant(soilState, level, soilPos, facing, plant);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!entity.isSteppingCarefully() && entity instanceof LivingEntity && level.getBlockEntity(pos) instanceof OwnableBlockEntity ownable && !ownable.isOwnedBy(entity))
			entity.hurt(level.damageSources().hotFloor(), 1.0F);

		super.stepOn(level, pos, state, entity);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BubbleColumnBlock.updateColumn(level, pos.above(), state);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.UP && facingState.is(Blocks.WATER))
			level.scheduleTick(currentPos, this, 20);

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		level.scheduleTick(pos, this, 20);
	}
}
