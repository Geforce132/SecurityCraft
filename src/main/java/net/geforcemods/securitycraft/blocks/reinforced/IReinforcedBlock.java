package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;

public interface IReinforcedBlock {
	public static final Map<Block, Block> VANILLA_TO_SECURITYCRAFT = new LinkedHashMap<>();
	public static final Map<Block, Block> SECURITYCRAFT_TO_VANILLA = new LinkedHashMap<>();

	public Block getVanillaBlock();

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public default BlockState getConvertedState(BlockState vanillaState) {
		BlockState defaultBlockState = ((Block) this).defaultBlockState();

		for (Property property : vanillaState.getProperties()) {
			if (defaultBlockState.hasProperty(property))
				defaultBlockState = defaultBlockState.setValue(property, vanillaState.getValue(property));
		}

		return defaultBlockState;
	}
}
