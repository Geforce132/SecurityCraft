package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedSlabs extends BlockSlab implements ITileEntityProvider {

	public static final String[] variants = new String[] {"stone", "cobble", "sand", "dirt", "stonebrick", "brick", "netherbrick", "quartz"};
    
	private final Material slabMaterial;

	public BlockReinforcedSlabs(boolean isDouble, Material par1Material) {
		super(isDouble, par1Material);
		
		this.slabMaterial = par1Material;
		this.useNeighborBrightness = true;
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
        super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
        par1World.removeTileEntity(par2, par3, par4);
    }

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List){
		if(slabMaterial != Material.ground){
			for(int i = 0; i < variants.length; i++){
				if(i == 3) //leave out space for dirt slab
					continue;
				
				par3List.add(new ItemStack(par1Item, 1, i));           
			}
		}else{
        	par3List.add(new ItemStack(par1Item, 1, 3));
        }
    }
	
	public Item getItemDropped(int par1, Random par2Random, int par3){
        return slabMaterial == Material.ground ? Item.getItemFromBlock(mod_SecurityCraft.reinforcedDirtSlab) : Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs);
    }
	
	@SideOnly(Side.CLIENT)
    public Item getItem(World par1World, int par2, int par3, int par4){
        return slabMaterial == Material.ground ? Item.getItemFromBlock(mod_SecurityCraft.reinforcedDirtSlab) : Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs);
    }
	
	/**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int par1){
        return new ItemStack(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 2, par1 & 7);
    }
    
    public String func_150002_b(int par1){
        if (par1 < 0 || par1 >= variants.length)
        {
        	par1 = 0;
        }

        return super.getUnlocalizedName() + "." + variants[par1];
    }
	
    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
		int block = getSlabBlock(par2);
		return Block.getBlockById(getSlabBlock(par2)).getIcon(par1, block == 24 && (par2 != 2 || par2 != 10) ? 0 : par2);
    }

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
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
	{
		return 0x999999;
	}
    
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int p_149741_1_)
    {
        return 0x999999;
    }
	
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
    	return 0x999999;
    }
    
	public TileEntity createNewTileEntity(World par1World, int par2) {
		return new TileEntityOwnable();
	}
}
