package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface IReinforcedBlock
{
	public static final Map<Block,Block> VANILLA_TO_SECURITYCRAFT = new HashMap<>();
	public static final Map<Block,Block> SECURITYCRAFT_TO_VANILLA = new HashMap<>();

	public Block getVanillaBlock();

	public BlockState getConvertedState(BlockState vanillaState);
}
