package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.FloorTrapBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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
	public VoxelShape getCollisionShapeWhenInvisible() {
		return Shapes.empty();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, SCContent.FLOOR_TRAP_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FloorTrapBlockEntity(pos, state);
	}
}
