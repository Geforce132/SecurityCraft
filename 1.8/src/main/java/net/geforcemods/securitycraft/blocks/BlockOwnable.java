package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOwnable extends BlockContainer {

	//if the color should be darkened, only used for reinforced stone/cobblestone/stone bricks/mossy cobblestone/bricks/nether bricks and dirt
	private boolean flag;
	
	public BlockOwnable(Material par1) {
		this(par1, false);
	}
	
	public BlockOwnable(Material mat, boolean f)
	{
		super(mat);
		flag = f;
	}
	
	public int getRenderType()
    {
        return 3;
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

	@Override
    @SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
	{
		return !flag ? super.colorMultiplier(worldIn, pos, renderPass) : 0x999999;
	}
	
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(IBlockState state)
    {
        return !flag ? super.getRenderColor(state) : 0x999999;
    }
	
    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
    	return !flag ? super.getBlockColor() : 0x999999;
    }
}
