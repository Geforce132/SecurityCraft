package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedStainedGlassPaneBlock extends ReinforcedPaneBlock implements BeaconBeamBlock {
	private final DyeColor color;

	public ReinforcedStainedGlassPaneBlock(BlockBehaviour.Properties properties, DyeColor color, Block vB) {
		super(properties, vB);
		this.color = color;
	}

	@Override
	public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		return color.getTextureDiffuseColor();
	}

	@Override
	public DyeColor getColor() {
		return color;
	}
}
