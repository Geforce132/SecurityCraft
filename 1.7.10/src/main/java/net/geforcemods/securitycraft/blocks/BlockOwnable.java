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

	//only used for reinforced blocks
	private Block type;
	//only true if it's a reinforced block
	private boolean flag = false;

	public BlockOwnable(Material par1) {
		super(par1);
	}

	//only used for reinforced blocks
	public BlockOwnable(Block t, Material mat)
	{
		super(mat);

		type = t;
		flag = true;
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2){
		return !flag ? super.getIcon(par1, par2) : type.getIcon(par1, par2);
	}

	@Override
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

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int p_149741_1_)
	{
		return !flag ? super.getRenderColor(p_149741_1_) : 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return !flag ? super.getBlockColor() : 0x999999;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}
