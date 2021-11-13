package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedMovingPistonBlock extends MovingPistonBlock {

	public ReinforcedMovingPistonBlock(Block.Properties properties) {
		super(properties);
	}

	public static BlockEntity newMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, CompoundTag tag, Direction direction, boolean extending, boolean shouldHeadBeRendered) {
		return new ReinforcedPistonMovingBlockEntity(pos, state, movedState, tag, direction, extending, shouldHeadBeRendered);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeReinforcedPiston, ReinforcedPistonMovingBlockEntity::tick);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			if (level.getBlockEntity(pos) instanceof ReinforcedPistonMovingBlockEntity pistonBlockEntity) {
				pistonBlockEntity.finalTick();
			}
		}
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		BlockPos oppositePos = pos.relative(state.getValue(FACING).getOpposite());
		BlockState oppositeState = level.getBlockState(oppositePos);

		if (oppositeState.getBlock() instanceof ReinforcedPistonBaseBlock && oppositeState.getValue(PistonBaseBlock.EXTENDED)) {
			level.removeBlock(oppositePos, false);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		if(builder.getLevel().getBlockEntity(new BlockPos(builder.getParameter(LootContextParams.ORIGIN))) instanceof ReinforcedPistonMovingBlockEntity be)
			return be.getMovedState().getDrops(builder);
		else return Collections.emptyList();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if(world.getBlockEntity(pos) instanceof ReinforcedPistonMovingBlockEntity be)
			return be.getCollisionShape(world, pos);
		else return Shapes.empty();
	}
}
