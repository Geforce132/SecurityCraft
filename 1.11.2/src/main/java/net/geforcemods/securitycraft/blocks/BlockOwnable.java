package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

public class BlockOwnable extends BlockContainer {

	private EnumBlockRenderType renderType = EnumBlockRenderType.MODEL;

	public BlockOwnable(Material material) {
		super(material);

		if(material == Material.GROUND)
			setSoundType(SoundType.GROUND);
		else if(material == Material.GLASS)
			setSoundType(SoundType.GLASS);
		else
			setSoundType(SoundType.STONE);
	}

	public BlockOwnable(Material material, EnumBlockRenderType renderType) {
		this(material);
		this.renderType = renderType;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return renderType;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}
