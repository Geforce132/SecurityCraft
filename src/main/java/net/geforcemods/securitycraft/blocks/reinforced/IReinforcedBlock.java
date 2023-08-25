package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public interface IReinforcedBlock {
	public static final Map<Block, Block> VANILLA_TO_SECURITYCRAFT = new LinkedHashMap<>();
	public static final Map<Block, Block> SECURITYCRAFT_TO_VANILLA = new LinkedHashMap<>();

	public Block getVanillaBlock();

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public default BlockState convertToReinforced(Level level, BlockPos pos, BlockState vanillaState) {
		BlockState defaultBlockState = ((Block) this).defaultBlockState();

		for (Property property : vanillaState.getProperties()) {
			if (defaultBlockState.hasProperty(property))
				defaultBlockState = defaultBlockState.setValue(property, vanillaState.getValue(property));
		}

		return defaultBlockState;
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public default BlockState convertToVanilla(Level level, BlockPos pos, BlockState reinforcedState) {
		BlockState defaultBlockState = getVanillaBlock().defaultBlockState();

		for (Property property : reinforcedState.getProperties()) {
			if (defaultBlockState.hasProperty(property))
				defaultBlockState = defaultBlockState.setValue(property, reinforcedState.getValue(property));
		}

		return defaultBlockState;
	}
}
