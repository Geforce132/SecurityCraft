package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOwnable extends BlockContainer {

	//only used for reinforced stone/cobblestone/stone bricks/mossy cobblestone/bricks/nether bricks and dirt
	private Block type;
	//only false if it's not reinforced stone/cobblestone/stone bricks/mossy cobblestone/bricks/nether bricks and dirt
	private boolean flag = true;
	
	public BlockOwnable(Material par1) {
		super(par1);
		flag = false;
	}
	
	//only used for reinforced stone/cobblestone/stone bricks/mossy cobblestone/bricks/nether bricks and dirt
	public BlockOwnable(Block t, Material mat)
	{
		super(mat);
		
		type = t;
	}

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
		return !flag ? super.getIcon(par1, par2) : type.getIcon(par1, par2);
    }

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		return !flag ? super.getIcon(access, x, y, z, side) : type.getIcon(side, access.getBlockMetadata(x, y, z));
	}
	
	@Override
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
	{
		return !flag ? super.colorMultiplier(p_149720_1_, p_149720_2_, p_149720_3_, p_149720_4_) : 0x999999;
	}
    
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int p_149741_1_)
    {
        return !flag ? super.getRenderColor(p_149741_1_) : 0x999999;
    }
	
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
    	return !flag ? super.getBlockColor() : 0x999999;
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}
