package net.breakinbad.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockReinforcedSlabs extends BlockSlab implements ITileEntityProvider {

	public static final String[] variants = new String[] {"stone", "cobble", "sand", "dirt"};
    
	@SideOnly(Side.CLIENT)
    private IIcon reinforcedStoneIcon;
	
	@SideOnly(Side.CLIENT)
    private IIcon reinforcedCobblestoneIcon;
	
	@SideOnly(Side.CLIENT)
    private IIcon reinforcedDirtIcon;
	
	@SideOnly(Side.CLIENT)
    private IIcon reinforcedSandstoneTopIcon;
	
	@SideOnly(Side.CLIENT)
    private IIcon reinforcedSandstoneIcon;
	
	private final boolean isDouble;
	private final Material slabMaterial;

	public BlockReinforcedSlabs(boolean isDouble, Material par1Material) {
		super(isDouble, par1Material);
		
		this.isDouble = isDouble;
		this.slabMaterial = par1Material;
		this.useNeighborBrightness = true;
	}

	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		if(!par1World.isRemote){
			if(par5EntityLivingBase instanceof EntityPlayer){
				((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
			}
		}
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
        super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
        par1World.removeTileEntity(par2, par3, par4);
    }

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List){
		if(slabMaterial != Material.ground){
			for(int i = 0; i < variants.length - 1; i++){
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
	
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
        int k = par2 & 7;

        if(k == 0){
        	return reinforcedStoneIcon;
        }else if(k == 1){
        	return reinforcedCobblestoneIcon;
        }else if(k == 2){
        	if(par1 == 1){
        		return reinforcedSandstoneTopIcon;
        	}else{
            	return reinforcedSandstoneIcon;
        	}
        }else if(k == 3){
        	return reinforcedDirtIcon;
        }else{
        	return blockIcon;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IIconRegister){
        this.blockIcon = par1IIconRegister.registerIcon("stone_slab_top");
        this.reinforcedStoneIcon = par1IIconRegister.registerIcon("securitycraft:reinforcedStone");
        this.reinforcedCobblestoneIcon = par1IIconRegister.registerIcon("securitycraft:reinforcedCobblestone");
        this.reinforcedDirtIcon = par1IIconRegister.registerIcon("securitycraft:reinforcedDirt");
        this.reinforcedSandstoneTopIcon = par1IIconRegister.registerIcon("securitycraft:reinforcedSandstone_top");
        this.reinforcedSandstoneIcon = par1IIconRegister.registerIcon("securitycraft:reinforcedSandstone_normal");
    }

	public TileEntity createNewTileEntity(World par1World, int par2) {
		return new TileEntityOwnable();
	}

}
