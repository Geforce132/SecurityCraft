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
	
	public BlockOwnable(Material par1) {
		super(par1);
		
		if(par1 == Material.GROUND)
			setSoundType(SoundType.GROUND);
		else
			setSoundType(SoundType.STONE);
	}
	
	public BlockOwnable(Material par1, EnumBlockRenderType par2RenderType) {
		this(par1);
		this.renderType = par2RenderType;
	}
	
	public EnumBlockRenderType getRenderType(IBlockState state) {
        return renderType;
    }
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}
}
