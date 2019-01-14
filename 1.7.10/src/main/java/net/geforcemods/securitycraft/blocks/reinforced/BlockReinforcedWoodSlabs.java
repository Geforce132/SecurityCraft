package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedWoodSlabs extends BlockSlab implements ITileEntityProvider {

	public static final String[] variants = new String[] {"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};

	public BlockReinforcedWoodSlabs(boolean isDouble) {
		super(isDouble, Material.wood);

		if(!isDouble)
			useNeighborBrightness = true;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
	 * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int meta)
	{
		return new ItemStack(SCContent.reinforcedWoodSlabs, 2, meta & 7);
	}

	@Override
	public String getFullSlabName(int meta){
		if(meta < 0 || meta >= variants.length)
			meta = 0;

		return super.getUnlocalizedName() + "." + variants[meta];
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

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		if(item != Item.getItemFromBlock(SCContent.reinforcedWoodPlanks))
			for(int i = 0; i < variants.length; i++)
				list.add(new ItemStack(item, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int u){
		return Item.getItemFromBlock(SCContent.reinforcedWoodSlabs);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return Item.getItemFromBlock(SCContent.reinforcedWoodSlabs);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return SCContent.reinforcedWoodPlanks.getIcon(side, meta & 7);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

}
