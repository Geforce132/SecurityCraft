package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.IBlockReader;

public class BlockOwnable extends BlockContainer {

	private EnumBlockRenderType renderType = EnumBlockRenderType.MODEL;

	public BlockOwnable(Block.Properties properties) {
		this(SoundType.STONE, properties);
	}

	public BlockOwnable(SoundType soundType, Block.Properties properties) {
		super(properties.sound(soundType));
	}

	public BlockOwnable(SoundType soundType, Block.Properties properties, EnumBlockRenderType renderType) {
		this(soundType, properties);
		this.renderType = renderType;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return renderType;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityOwnable();
	}
}
