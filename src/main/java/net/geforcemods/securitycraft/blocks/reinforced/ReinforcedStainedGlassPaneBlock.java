package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.item.DyeColor;

public class ReinforcedStainedGlassPaneBlock extends ReinforcedPaneBlock implements IBeaconBeamColorProvider {
	public ReinforcedStainedGlassPaneBlock(AbstractBlock.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public DyeColor getColor() {
		return getVanillaBlock() instanceof IBeaconBeamColorProvider ? ((IBeaconBeamColorProvider) getVanillaBlock()).getColor() : DyeColor.WHITE;
	}
}
