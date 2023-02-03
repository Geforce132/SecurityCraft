package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;

public class CustomStairsBlock extends BlockStairs {
	public CustomStairsBlock(IBlockState state) {
		super(state);
		useNeighborBrightness = true;
	}
}
