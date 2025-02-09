package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedStainedGlassBlock extends ReinforcedGlassBlock implements IBeaconBeamColorProvider {
	public ReinforcedStainedGlassBlock(AbstractBlock.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader level, BlockPos pos) {
		return true;
	}

	@Override
	public DyeColor getColor() {
		return getVanillaBlock() instanceof IBeaconBeamColorProvider ? ((IBeaconBeamColorProvider) getVanillaBlock()).getColor() : DyeColor.WHITE;
	}

	@Override
	public float getShadeBrightness(BlockState state, IBlockReader level, BlockPos pos) {
		return 1.0F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.getBlock() == this || super.skipRendering(state, adjacentBlockState, side);
	}
}
