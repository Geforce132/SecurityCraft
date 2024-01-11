package net.geforcemods.securitycraft.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public interface IReinforcedBlock {
	public static final Map<Block, Block> VANILLA_TO_SECURITYCRAFT = new LinkedHashMap<>();

	public List<Block> getVanillaBlocks();

	public default IBlockState convertToReinforcedState(IBlockState state) {
		return ((Block) this).getStateFromMeta(state.getBlock().getMetaFromState(state));
	}

	public default IBlockState convertToVanillaState(IBlockState state) throws Exception {
		List<Block> vanillaBlocks = getVanillaBlocks();

		if (vanillaBlocks.size() > 1)
			throw new Exception(state.getBlock().getRegistryName() + " has more than 1 vanilla counterpart, but does not override convertToVanillaState! Cannot determine vanilla state.");

		return vanillaBlocks.get(0).getStateFromMeta(state.getBlock().getMetaFromState(state));
	}

	public default ItemStack convertToReinforcedStack(ItemStack stackToConvert, Block blockToConvert) {
		return new ItemStack((Block) this, 1, stackToConvert.getMetadata());
	}

	public default ItemStack convertToVanillaStack(ItemStack stackToConvert) throws Exception {
		List<Block> vanillaBlocks = getVanillaBlocks();

		if (vanillaBlocks.size() > 1)
			throw new Exception(stackToConvert.getItem().getRegistryName() + " has more than 1 vanilla counterpart, but does not override convertToVanillaStack! Cannot determine vanilla stack.");

		return new ItemStack(vanillaBlocks.get(0), 1, stackToConvert.getMetadata());
	}
}
