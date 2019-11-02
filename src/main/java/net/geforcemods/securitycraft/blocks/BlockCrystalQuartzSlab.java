package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrystalQuartzSlab extends BlockSlab
{
	public BlockCrystalQuartzSlab(boolean isDouble, Material material)
	{
		super(isDouble, material);

		useNeighborBrightness = true;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public String getFullSlabName(int meta)
	{
		return super.getUnlocalizedName();
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		int block = getSlabBlock(meta);
		return Block.getBlockById(getSlabBlock(meta)).getIcon(side, block == 24 && (meta != 2 || meta != 10) ? 0 : meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		int meta = access.getBlockMetadata(x, y, z);
		int block = getSlabBlock(meta);

		return Block.getBlockById(block).getIcon(side, block == 24 && (meta != 2 || meta != 10) ? 0 : meta);
	}

	/**
	 * Gets the type of slab by the metadata
	 * @param meta The metadata of the slab
	 * @return The type of this slab, 0 if invalid
	 */
	private int getSlabBlock(int meta)
	{
		switch(meta)
		{
			case 0: case 8: return Block.getIdFromBlock(SCContent.crystalQuartz);
		}

		return 0;
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return 0x15b3a2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return 0x15b3a2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x15b3a2;
	}
}
