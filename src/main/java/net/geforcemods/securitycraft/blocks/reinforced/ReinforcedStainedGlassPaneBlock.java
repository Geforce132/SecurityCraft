package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ReinforcedStainedGlassPaneBlock extends ReinforcedPaneBlock implements IBeaconBeamColorProvider {
	private final DyeColor color;

	public ReinforcedStainedGlassPaneBlock(AbstractBlock.Properties properties, DyeColor color, Block vB) {
		super(properties, vB);
		this.color = color;
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader level, BlockPos pos, BlockPos beaconPos) {
		return color.getTextureDiffuseColors();
	}

	@Override
	public DyeColor getColor() {
		return color;
	}
}
