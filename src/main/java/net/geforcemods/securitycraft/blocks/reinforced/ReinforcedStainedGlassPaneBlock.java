package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ReinforcedStainedGlassPaneBlock extends ReinforcedPaneBlock implements BeaconBeamBlock {
	public ReinforcedStainedGlassPaneBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public DyeColor getColor() {
		return getVanillaBlock() instanceof BeaconBeamBlock beaconBeamBlock ? beaconBeamBlock.getColor() : DyeColor.WHITE;
	}
}
