package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
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
import net.minecraft.world.level.block.RootsBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.WitherRoseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class BaseReinforcedBlock extends OwnableBlock implements IReinforcedBlock {
	private final Supplier<Block> vanillaBlockSupplier;

	public BaseReinforcedBlock(Block.Properties properties, Block vB) {
		this(properties, () -> vB);
	}

	public BaseReinforcedBlock(Block.Properties properties, Supplier<Block> vB) {
		super(properties);

		vanillaBlockSupplier = vB;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
		BlockState plant = plantable.getPlant(level, pos.relative(facing));
		PlantType type = plantable.getPlantType(level, pos.relative(facing));

		if (super.canSustainPlant(state, level, pos, facing, plantable))
			return true;

		if (plant.getBlock() == Blocks.CACTUS)
			return state.is(SCTags.Blocks.REINFORCED_SAND);

		if (plantable instanceof BushBlock) { //a nasty workaround because BaseReinforcedBlock can't use BushBlock#mayPlaceOn because it is protected
			boolean condition = false;

			if (plantable instanceof AzaleaBlock || plantable instanceof MangrovePropaguleBlock)
				condition = state.is(SCContent.REINFORCED_CLAY.get()) || state.is(SCTags.Blocks.REINFORCED_DIRT);
			else if (plantable instanceof FungusBlock || plantable instanceof NetherSproutsBlock || plantable instanceof RootsBlock)
				condition = state.is(SCContent.REINFORCED_SOUL_SOIL.get()) || state.is(SCTags.Blocks.REINFORCED_DIRT);
			else if (plantable instanceof WaterlilyBlock)
				condition = level.getFluidState(pos).getType() == SCContent.FAKE_WATER.get() && level.getFluidState(pos.above()).getType() == Fluids.EMPTY;
			else if (plantable instanceof WitherRoseBlock)
				condition = state.is(SCContent.REINFORCED_NETHERRACK.get()) || state.is(SCContent.REINFORCED_SOUL_SOIL.get()) || state.is(SCTags.Blocks.REINFORCED_DIRT);

			if (condition)
				return true;
		}

		if (PlantType.DESERT.equals(type))
			return this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_TERRACOTTA.get() || this instanceof ReinforcedGlazedTerracottaBlock;
		else if (PlantType.PLAINS.equals(type))
			return state.is(SCTags.Blocks.REINFORCED_DIRT);
		else if (PlantType.BEACH.equals(type)) {
			boolean isBeach = state.is(SCTags.Blocks.REINFORCED_DIRT) || state.is(SCTags.Blocks.REINFORCED_SAND);
			boolean hasWater = false;

			for (Direction face : Direction.Plane.HORIZONTAL) {
				BlockState blockState = level.getBlockState(pos.relative(face));
				FluidState fluidState = level.getFluidState(pos.relative(face));

				hasWater |= blockState.is(Blocks.FROSTED_ICE);
				hasWater |= fluidState.canHydrate(level, pos, blockState, pos.relative(face));

				if (hasWater)
					break; //No point continuing.
			}

			return isBeach && hasWater;
		}

		return false;
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
			return adjacentBlockState.getBlock() == this ? true : super.skipRendering(state, adjacentBlockState, side);

		return false;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		PushReaction originalPushReaction = super.getPistonPushReaction(state);

		return originalPushReaction == PushReaction.DESTROY ? PushReaction.NORMAL : originalPushReaction;
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlockSupplier.get();
	}
}
