package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedQuartz extends BlockOwnable
{
	public BlockReinforcedQuartz()
	{
		super(Material.rock);
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return Blocks.quartz_block.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
	{
		p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
		p_149666_3_.add(new ItemStack(p_149666_1_, 1, 1));
		p_149666_3_.add(new ItemStack(p_149666_1_, 1, 2));
	}

	@Override
	public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
	{
		if (p_149660_9_ == 2)
			switch (p_149660_5_)
			{
				case 0:
				case 1:
					p_149660_9_ = 2;
					break;
				case 2:
				case 3:
					p_149660_9_ = 4;
					break;
				case 4:
				case 5:
					p_149660_9_ = 3;
			}

		return p_149660_9_;
	}

	@Override
	public int damageDropped(int p_149692_1_)
	{
		return p_149692_1_ != 3 && p_149692_1_ != 4 ? p_149692_1_ : 2;
	}

	@Override
	protected ItemStack createStackedBlock(int p_149644_1_)
	{
		return p_149644_1_ != 3 && p_149644_1_ != 4 ? super.createStackedBlock(p_149644_1_) : new ItemStack(Item.getItemFromBlock(this), 1, 2);
	}

	@Override
	public int getRenderType()
	{
		return 39;
	}

	@Override
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int p_149741_1_)
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
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if(meta > 2)
			meta = 2;

		return new ItemStack(Item.getItemFromBlock(this), 1, meta);
	}
}
