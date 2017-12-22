package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;

public interface IReinforcedBlock
{
	public static final List<Block> BLOCKS = Arrays.asList(new Block[] {
			mod_SecurityCraft.reinforcedBrick,
			mod_SecurityCraft.reinforcedCobblestone,
			mod_SecurityCraft.reinforcedCompressedBlocks,
			mod_SecurityCraft.reinforcedConcrete,
			mod_SecurityCraft.reinforcedDirt,
			mod_SecurityCraft.reinforcedEndStoneBricks,
			mod_SecurityCraft.reinforcedGlass,
			mod_SecurityCraft.reinforcedHardenedClay,
			mod_SecurityCraft.unbreakableIronBars,
			mod_SecurityCraft.reinforcedMetals,
			mod_SecurityCraft.reinforcedMossyCobblestone,
			mod_SecurityCraft.reinforcedNetherBrick,
			mod_SecurityCraft.reinforcedNewLogs,
			mod_SecurityCraft.reinforcedOldLogs,
			mod_SecurityCraft.reinforcedPrismarine,
			mod_SecurityCraft.reinforcedPurpur,
			mod_SecurityCraft.reinforcedQuartz,
			mod_SecurityCraft.reinforcedRedNetherBrick,
			mod_SecurityCraft.reinforcedRedSandstone,
			mod_SecurityCraft.reinforcedSandstone,
			mod_SecurityCraft.reinforcedStainedHardenedClay,
			mod_SecurityCraft.reinforcedStone,
			mod_SecurityCraft.reinforcedStoneBrick,
			mod_SecurityCraft.reinforcedWoodPlanks,
			mod_SecurityCraft.reinforcedWool
	});

	public List<Block> getVanillaBlocks();

	public int getAmount();
}
