package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.FloorTrapBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FloorTrapBlock extends SometimesVisibleBlock {
	public FloorTrapBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(INVISIBLE, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (pos.getY() == neighborPos.getY() && level.getBlockEntity(pos) instanceof FloorTrapBlockEntity trap1 && trap1.isModuleEnabled(ModuleType.SMART) && level.getBlockEntity(neighborPos) instanceof FloorTrapBlockEntity trap2 && trap1.getOwner().owns(trap2) && level.getBlockState(neighborPos).getValue(INVISIBLE)) {
			if (trap1.shouldDisappearInstantlyInChains())
				trap1.scheduleDisappear(0, true);
			else
				trap1.scheduleDisappear(true);
		}
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
		return (adjacentState.is(this) && !adjacentState.getValue(INVISIBLE)) || super.skipRendering(state, adjacentState, side);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public VoxelShape getCollisionShapeWhenInvisible() {
		return Shapes.empty();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide)
			return state.getValue(INVISIBLE) ? createTickerHelper(type, SCContent.FLOOR_TRAP_BLOCK_ENTITY.get(), FloorTrapBlockEntity::particleTick) : null;

		return createTickerHelper(type, SCContent.FLOOR_TRAP_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FloorTrapBlockEntity(pos, state);
	}
}
