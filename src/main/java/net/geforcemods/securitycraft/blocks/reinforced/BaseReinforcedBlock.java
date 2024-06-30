package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.NetherSproutsBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.RootsBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.WitherRoseBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.TriState;

public class BaseReinforcedBlock extends OwnableBlock implements IReinforcedBlock {
	private final Supplier<? extends Block> vanillaBlockSupplier;

	public BaseReinforcedBlock(Block vB) {
		this(SCContent.reinforcedCopy(vB), () -> vB);
	}

	public BaseReinforcedBlock(BlockBehaviour.Properties properties, Block vB) {
		this(properties, () -> vB);
	}

	public BaseReinforcedBlock(BlockBehaviour.Properties properties, Supplier<? extends Block> vB) {
		super(properties);

		vanillaBlockSupplier = vB;
	}

	@Override
	public TriState canSustainPlant(BlockState soilState, BlockGetter level, BlockPos soilPos, Direction facing, BlockState plant) {
		Block plantable = plant.getBlock();

		if (plant.is(Blocks.CACTUS))
			return soilState.is(SCTags.Blocks.REINFORCED_SAND) ? TriState.TRUE : TriState.FALSE;
		else if (plantable instanceof BushBlock) { //a nasty workaround because BaseReinforcedBlock can't use BushBlock#mayPlaceOn because it is protected
			boolean condition = false;

			if (plantable instanceof AzaleaBlock || plantable instanceof MangrovePropaguleBlock)
				condition = soilState.is(SCContent.REINFORCED_CLAY.get()) || soilState.is(SCTags.Blocks.REINFORCED_DIRT);
			else if (plantable instanceof FungusBlock || plantable instanceof NetherSproutsBlock || plantable instanceof RootsBlock)
				condition = soilState.is(SCContent.REINFORCED_SOUL_SOIL.get()) || soilState.is(SCTags.Blocks.REINFORCED_DIRT);
			else if (plantable instanceof WaterlilyBlock)
				condition = level.getFluidState(soilPos).getType() == SCContent.FAKE_WATER.get() && level.getFluidState(soilPos.above()).getType() == Fluids.EMPTY;
			else if (plantable instanceof WitherRoseBlock)
				condition = soilState.is(SCContent.REINFORCED_NETHERRACK.get()) || soilState.is(SCContent.REINFORCED_SOUL_SAND.get()) || soilState.is(SCContent.REINFORCED_SOUL_SOIL.get()) || soilState.is(SCTags.Blocks.REINFORCED_DIRT);
			else if (plantable instanceof NetherWartBlock)
				condition = soilState.is(SCContent.REINFORCED_SOUL_SAND.get());

			if (condition)
				return TriState.TRUE;
		}
		else if (plant.is(Blocks.SUGAR_CANE)) {
			boolean isBeach = soilState.is(SCTags.Blocks.REINFORCED_DIRT) || soilState.is(SCTags.Blocks.REINFORCED_SAND);
			boolean hasWater = false;

			for (Direction face : Direction.Plane.HORIZONTAL) {
				BlockState blockState = level.getBlockState(soilPos.relative(face));
				FluidState fluidState = level.getFluidState(soilPos.relative(face));

				hasWater |= blockState.is(Blocks.FROSTED_ICE);
				hasWater |= fluidState.canHydrate(level, soilPos, blockState, soilPos.relative(face));

				if (hasWater)
					break; //No point continuing.
			}

			return isBeach && hasWater ? TriState.TRUE : TriState.FALSE;
		}

		return TriState.DEFAULT;
	}

	@Override
	public boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
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
