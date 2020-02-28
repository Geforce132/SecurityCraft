package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ReinforcedRotatedCrystalQuartzPillar extends ReinforcedRotatedPillarBlock implements IBlockPocket
{
	public ReinforcedRotatedCrystalQuartzPillar(Material mat, Supplier<Block> vB)
	{
		super(SoundType.STONE, mat, vB);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new BlockPocketTileEntity();
	}
}
