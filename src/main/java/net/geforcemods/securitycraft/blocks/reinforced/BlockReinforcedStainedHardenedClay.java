package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockReinforcedStainedHardenedClay extends BlockOwnable implements IReinforcedBlock
{
	private static final IIcon[] iicons = new IIcon[16];

	public BlockReinforcedStainedHardenedClay()
	{
		super(Material.rock);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	@SideOnly(Side.CLIENT)
	public int flipMeta(int meta){
		return ~meta & 15;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for (int i = 0; i < iicons.length; ++i)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public int damageDropped(int meta){
		return meta;
	}

	@Override
	public int quantityDropped(Random random){
		return 1;
	}

	@Override
	protected boolean canSilkHarvest(){
		return true;
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return Blocks.stained_hardened_clay.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		return Blocks.stained_hardened_clay.getIcon(side, access.getBlockMetadata(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		for(int i = 0; i < iicons.length; ++i)
			iicons[i] = register.registerIcon(getTextureName() + "_" + ItemDye.dyeIcons[flipMeta(i)]);
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
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.stained_hardened_clay
		});
	}

	@Override
	public int getAmount()
	{
		return 16;
	}
}
