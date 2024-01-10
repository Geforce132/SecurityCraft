package net.geforcemods.securitycraft.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public interface IReinforcedBlock {
	public static final Map<Block, Block> VANILLA_TO_SECURITYCRAFT = new LinkedHashMap<>();

	public List<Block> getVanillaBlocks();

	public default IBlockState convertToReinforced(IBlockState state) {
		return ((Block) this).getStateFromMeta(state.getBlock().getMetaFromState(state));
	}

	public default IBlockState convertToVanilla(IBlockState state) {
		return ((Block) this).getStateFromMeta(state.getBlock().getMetaFromState(state));
	}
}
