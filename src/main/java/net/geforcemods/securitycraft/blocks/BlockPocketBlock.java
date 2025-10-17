package net.geforcemods.securitycraft.blocks;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPocketBlock extends BaseReinforcedBlock implements IBlockPocket {
	public BlockPocketBlock(BlockBehaviour.Properties properties, Supplier<Block> vB) {
		super(properties, vB);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof BlockPocketBlockEntity be) {
			BlockPocketManagerBlockEntity manager = be.getManager();

			if (manager != null)
				manager.disableMultiblock();
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlockPocketBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.BLOCK_POCKET_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
	}
}
