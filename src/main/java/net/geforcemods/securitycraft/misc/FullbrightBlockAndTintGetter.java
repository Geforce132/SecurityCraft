package net.geforcemods.securitycraft.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

public class FullbrightBlockAndTintGetter implements IBlockDisplayReader {
	private final IBlockDisplayReader original;
	private final WorldLightManager emptyLightEngine;

	public FullbrightBlockAndTintGetter(IBlockDisplayReader original) {
		this.original = original;
		emptyLightEngine = new WorldLightManager(null, false, false);
	}

	@Override
	public float getShade(Direction direction, boolean shade) {
		return original.getShade(direction, shade);
	}

	@Override
	public WorldLightManager getLightEngine() {
		return emptyLightEngine;
	}

	@Override
	public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
		return original.getBlockTint(pos, colorResolver);
	}

	@Override
	public int getBrightness(LightType lightType, BlockPos pos) {
		return 15;
	}

	@Override
	public int getRawBrightness(BlockPos pos, int amount) {
		return 15;
	}

	@Override
	public TileEntity getBlockEntity(BlockPos pos) {
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
}
