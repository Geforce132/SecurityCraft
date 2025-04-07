package net.geforcemods.securitycraft.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FullbrightBlockAndTintGetter implements BlockAndTintGetter {
	private final BlockAndTintGetter original;

	public FullbrightBlockAndTintGetter(BlockAndTintGetter original) {
		this.original = original;
	}

	@Override
	public float getShade(Direction direction, boolean shade) {
		return original.getShade(direction, shade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return LevelLightEngine.EMPTY;
	}

	@Override
	public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
		return original.getBlockTint(blockPos, colorResolver);
	}

	@Override
	public int getBrightness(LightLayer lightType, BlockPos blockPos) {
		return 15;
	}

	@Override
	public int getRawBrightness(BlockPos blockPos, int amount) {
		return 15;
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos p_363675_) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos p_364801_) {
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public FluidState getFluidState(BlockPos p_364619_) {
		return Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getMinY() {
		return 0;
	}
}
