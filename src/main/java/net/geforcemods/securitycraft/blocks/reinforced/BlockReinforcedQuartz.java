package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedQuartz extends BlockOwnable implements IReinforcedBlock
{
	public BlockReinforcedQuartz()
	{
		super(Material.rock);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return Blocks.quartz_block.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		if (meta == 2)
			switch (side)
			{
				case 0:
				case 1:
					meta = 2;
					break;
				case 2:
				case 3:
					meta = 4;
					break;
				case 4:
				case 5:
					meta = 3;
			}

		return meta;
	}

	@Override
	public int damageDropped(int meta)
	{
		return meta != 3 && meta != 4 ? meta : 2;
	}

	@Override
	protected ItemStack createStackedBlock(int meta)
	{
		return meta != 3 && meta != 4 ? super.createStackedBlock(meta) : new ItemStack(Item.getItemFromBlock(this), 1, 2);
	}

	@Override
	public int getRenderType()
	{
		return 39;
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
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if(meta > 2)
			meta = 2;

		return new ItemStack(Item.getItemFromBlock(this), 1, meta);
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.quartz_block
		});
	}

	@Override
	public int getAmount()
	{
		return 5;
	}
}
