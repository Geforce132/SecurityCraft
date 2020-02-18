package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ProjectorBlock extends OwnableBlock {

	public ProjectorBlock(Properties properties) {
		super(SoundType.METAL, properties);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ProjectorTileEntity();
	}

}
