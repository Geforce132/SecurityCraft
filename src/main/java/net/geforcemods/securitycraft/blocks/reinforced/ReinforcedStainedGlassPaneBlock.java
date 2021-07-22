package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class ReinforcedStainedGlassPaneBlock extends ReinforcedPaneBlock implements BeaconBeamBlock
{
	private final DyeColor color;

	public ReinforcedStainedGlassPaneBlock(Block.Properties properties, DyeColor color, Block vB)
	{
		super(properties, vB);
		this.color = color;
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos)
	{
		return color.getTextureDiffuseColors();
	}

	@Override
	public DyeColor getColor()
	{
		return color;
	}
}
