package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedCrystalQuartzSlab extends BlockSlab implements ITileEntityProvider
{
	public BlockReinforcedCrystalQuartzSlab(boolean isDouble, Material material)
	{
		super(isDouble, material);

		useNeighborBrightness = true;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5Block, int par6)
	{
		super.breakBlock(world, x, y, z, par5Block, par6);
		world.removeTileEntity(x, y, z);
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
			case 0: case 8: return Block.getIdFromBlock(SCContent.reinforcedCrystalQuartz);
		}

		return 0;
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return 0x0E7063;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return 0x0E7063;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x0E7063;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}
