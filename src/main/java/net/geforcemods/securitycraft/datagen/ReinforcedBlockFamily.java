package net.geforcemods.securitycraft.datagen;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record ReinforcedBlockFamily(BlockFamily family, Block reinforcedBaseBlock) {
	public ReinforcedBlockFamily(BlockFamily family) {
		this(family, IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(family.getBaseBlock()));

		if (reinforcedBaseBlock == null)
			throw new IllegalStateException("Couldn't find reinforced block for " + Utils.getRegistryName(family.getBaseBlock()));
	}

	public Map<BlockFamily.Variant, Block> getVariants() {
		return family.getVariants().entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> get(e.getKey())));
	}

	public Block get(BlockFamily.Variant variant) {
		return IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.getOrDefault(family.get(variant), Blocks.AIR);
	}
}
