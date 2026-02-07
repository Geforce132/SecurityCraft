package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class BaseReinforcedBlock extends OwnableBlock implements IReinforcedBlock {
	private final Supplier<? extends Block> vanillaBlockSupplier;

	public BaseReinforcedBlock(BlockBehaviour.Properties properties, Block vB) {
		this(properties, () -> vB);
	}

	public BaseReinforcedBlock(BlockBehaviour.Properties properties, Supplier<? extends Block> vB) {
		super(properties);

		vanillaBlockSupplier = vB;
	}

	@Override
	public boolean onTreeGrow(BlockState state, WorldGenLevel level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		return true; //Do not allow trees to replace reinforced blocks with dirt when growing
	}

	@Override
	public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
		return this == SCContent.REINFORCED_PRISMARINE.get() || this == SCContent.REINFORCED_PRISMARINE_BRICKS.get() || this == SCContent.REINFORCED_SEA_LANTERN.get() || this == SCContent.REINFORCED_DARK_PRISMARINE.get();
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (getVanillaBlock() instanceof HalfTransparentBlock)
			return adjacentBlockState.getBlock() == this || super.skipRendering(state, adjacentBlockState, side);

		return false;
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlockSupplier.get();
	}
}
