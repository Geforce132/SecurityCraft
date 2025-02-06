package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class ReinforcedMagmaBlock extends BaseReinforcedBlock {
	public ReinforcedMagmaBlock(BlockBehaviour.Properties properties, Block vanillaBlock) {
		super(properties, vanillaBlock);
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
		if (plantable instanceof SeagrassBlock || plantable instanceof TallSeagrassBlock)
			return false;

		return super.canSustainPlant(state, level, pos, facing, plantable);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!entity.isSteppingCarefully() && entity instanceof LivingEntity lEntity && !EnchantmentHelper.hasFrostWalker(lEntity) && level.getBlockEntity(pos) instanceof OwnableBlockEntity ownable && !ownable.isOwnedBy(entity))
			entity.hurt(level.damageSources().hotFloor(), 1.0F);

		super.stepOn(level, pos, state, entity);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BubbleColumnBlock.updateColumn(level, pos.above(), state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.UP && facingState.is(Blocks.WATER))
			level.scheduleTick(currentPos, this, 20);

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		level.scheduleTick(pos, this, 20);
	}
}
