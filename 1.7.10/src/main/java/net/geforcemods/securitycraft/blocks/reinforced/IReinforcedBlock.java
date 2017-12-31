package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;

public interface IReinforcedBlock
{
	public static final List<Block> BLOCKS = Arrays.asList(new Block[] {
			SCContent.reinforcedBrick,
			SCContent.reinforcedCobblestone,
			SCContent.reinforcedCompressedBlocks,
			SCContent.reinforcedDirt,
			SCContent.reinforcedGlass,
			SCContent.reinforcedGlassPane,
			SCContent.reinforcedHardenedClay,
			SCContent.unbreakableIronBars,
			SCContent.reinforcedMetals,
			SCContent.reinforcedMossyCobblestone,
			SCContent.reinforcedNetherBrick,
			SCContent.reinforcedNewLogs,
			SCContent.reinforcedOldLogs,
			SCContent.reinforcedQuartz,
			SCContent.reinforcedSandstone,
			SCContent.reinforcedStainedHardenedClay,
			SCContent.reinforcedStone,
			SCContent.reinforcedStoneBrick,
			SCContent.reinforcedWoodPlanks,
			SCContent.reinforcedWool
	});

	public List<Block> getVanillaBlocks();

	public int getAmount();
}
