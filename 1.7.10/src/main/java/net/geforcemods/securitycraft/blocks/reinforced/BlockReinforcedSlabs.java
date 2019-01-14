package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedSlabs extends BlockSlab implements ITileEntityProvider {

	public static final String[] variants = new String[] {"stone", "cobble", "sand", "dirt", "stonebrick", "brick", "netherbrick", "quartz"};

	private final Material slabMaterial;

	public BlockReinforcedSlabs(boolean isDouble, Material material) {
		super(isDouble, material);

		slabMaterial = material;
		useNeighborBrightness = true;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5Block, int par6){
		super.breakBlock(world, x, y, z, par5Block, par6);
		world.removeTileEntity(x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		if(slabMaterial != Material.ground)
			for(int i = 0; i < variants.length; i++){
				if(i == 3) //leave out space for dirt slab
					continue;

				list.add(new ItemStack(item, 1, i));
			}
		else
			list.add(new ItemStack(item, 1, 3));
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return slabMaterial == Material.ground ? Item.getItemFromBlock(SCContent.reinforcedDirtSlab) : Item.getItemFromBlock(SCContent.reinforcedStoneSlabs);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return slabMaterial == Material.ground ? Item.getItemFromBlock(SCContent.reinforcedDirtSlab) : Item.getItemFromBlock(SCContent.reinforcedStoneSlabs);
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
	 * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int meta){
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 2, meta & 7);
	}

	@Override
	public String getFullSlabName(int meta){
		if (meta < 0 || meta >= variants.length)
			meta = 0;

		return super.getUnlocalizedName() + "." + variants[meta];
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
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
			case 0: case 8: return 44;
			case 1: case 9: return 4;
			case 2: case 10: return 24;
			case 3: case 11: return 3;
			case 4: case 12: return 98;
			case 5: case 13: return 45;
			case 6: case 14: return 112;
			case 7: case 15: return 155;
		}

		return 0;
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}
