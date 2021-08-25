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
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity te = worldIn.getBlockEntity(pos);

			if (te instanceof ReinforcedPistonMovingBlockEntity pistonBlockEntity) {
				pistonBlockEntity.finalTick();
			}

		}
	}

	/**
	 * Called after this block has been removed by a player.
	 */
	@Override
	public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (blockstate.getBlock() instanceof ReinforcedPistonBaseBlock && blockstate.getValue(PistonBaseBlock.EXTENDED)) {
			worldIn.removeBlock(blockpos, false);
		}

	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ReinforcedPistonMovingBlockEntity reinforcedPistonMovingBlockEntity = this.getBlockEntity(builder.getLevel(), new BlockPos(builder.getParameter(LootContextParams.ORIGIN)));
		return reinforcedPistonMovingBlockEntity == null ? Collections.emptyList() : reinforcedPistonMovingBlockEntity.getMovedState().getDrops(builder);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		ReinforcedPistonMovingBlockEntity reinforcedPistonMovingBlockEntity = this.getBlockEntity(world, pos);

		return reinforcedPistonMovingBlockEntity != null ? reinforcedPistonMovingBlockEntity.getCollisionShape(world, pos) : Shapes.empty();
	}

	private ReinforcedPistonMovingBlockEntity getBlockEntity(BlockGetter world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);

		return te instanceof ReinforcedPistonMovingBlockEntity ? (ReinforcedPistonMovingBlockEntity)te : null;
	}
}
