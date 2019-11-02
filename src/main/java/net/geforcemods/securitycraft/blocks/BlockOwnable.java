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
	private boolean darkenBlock = false;

	public BlockOwnable(Material material) {
		super(material);
	}

	//only used for reinforced blocks
	public BlockOwnable(Block t, Material mat)
	{
		super(mat);

		type = t;
		darkenBlock = true;
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return !darkenBlock ? super.getIcon(side, meta) : type.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		return !darkenBlock ? super.getIcon(access, x, y, z, side) : type.getIcon(side, access.getBlockMetadata(x, y, z));
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return !darkenBlock ? super.colorMultiplier(access, x, y, z) : 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return !darkenBlock ? super.getRenderColor(meta) : 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return !darkenBlock ? super.getBlockColor() : 0x999999;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

}
