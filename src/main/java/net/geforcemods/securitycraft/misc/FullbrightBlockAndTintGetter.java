package net.geforcemods.securitycraft.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FullbrightBlockAndTintGetter implements BlockAndTintGetter {
	private final BlockAndTintGetter original;
	private final LevelLightEngine emptyLightEngine;

	public FullbrightBlockAndTintGetter(Level original) {
		this.original = original;
		emptyLightEngine = new LevelLightEngine(original.getChunkSource(), false, false);
		emptyLightEngine.levelHeightAccessor = LevelHeightAccessor.create(0, 0);
	}

	@Override
	public float getShade(Direction direction, boolean shade) {
		return original.getShade(direction, shade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return emptyLightEngine;
	}

	@Override
	public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
		return original.getBlockTint(pos, colorResolver);
	}

	@Override
	public int getBrightness(LightLayer lightType, BlockPos pos) {
		return 15;
	}

	@Override
	public int getRawBrightness(BlockPos pos, int amount) {
		return 15;
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getMinBuildHeight() {
		return 0;
	}
}
