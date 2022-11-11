package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public interface IReinforcedBlock {
	public static final Map<Block, Block> VANILLA_TO_SECURITYCRAFT = new HashMap<>();
	public static final Map<Block, Block> SECURITYCRAFT_TO_VANILLA = new HashMap<>();

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
