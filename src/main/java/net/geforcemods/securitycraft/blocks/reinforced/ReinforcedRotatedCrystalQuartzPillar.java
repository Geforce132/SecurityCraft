package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
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

public class ReinforcedRotatedCrystalQuartzPillar extends ReinforcedRotatedPillarBlock implements IBlockPocket {
	public ReinforcedRotatedCrystalQuartzPillar(BlockBehaviour.Properties properties, Supplier<Block> vB) {
		super(properties, vB);
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
