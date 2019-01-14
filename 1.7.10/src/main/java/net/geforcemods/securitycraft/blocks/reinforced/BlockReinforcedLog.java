package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedLog extends BlockOwnable implements ICustomWailaDisplay
{
	public BlockReinforcedLog()
	{
		super(Material.wood);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	@Override
	public int getRenderType()
	{
		return 31;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		int type = meta & 3;
		byte orientation = 0;

		switch (side)
		{
			case 0:
			case 1:
				orientation = 0;
				break;
			case 2:
			case 3:
				orientation = 8;
				break;
			case 4:
			case 5:
				orientation = 4;
		}

		return type | orientation;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		byte b0 = 4;
		int i1 = b0 + 1;

		if (world.checkChunksExist(x - i1, y - i1, z - i1, x + i1, y + i1, z + i1))
			for (int j1 = -b0; j1 <= b0; ++j1)
				for (int k1 = -b0; k1 <= b0; ++k1)
					for (int l1 = -b0; l1 <= b0; ++l1)
					{
						Block potentialLeaves = world.getBlock(x + j1, y + k1, z + l1);
						if (potentialLeaves.isLeaves(world, x + j1, y + k1, z + l1))
							potentialLeaves.beginLeavesDecay(world, x + j1, y + k1, z + l1);
					}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(this instanceof BlockReinforcedOldLog)
			return Blocks.log.getIcon(side, meta);
		else
			return Blocks.log2.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		return getIcon(side, access.getBlockMetadata(x, y, z));
	}

	@Override
	public int damageDropped(int meta)
	{
		return meta & 3;
	}

	@Override
	protected ItemStack createStackedBlock(int meta)
	{
		return new ItemStack(Item.getItemFromBlock(this), 1, damageDropped(meta));
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x999999;
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z)
	{
		return new ItemStack(Item.getItemFromBlock(world.getBlock(x, y, z)), 1, world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z)
	{
		return true;
	}
}
