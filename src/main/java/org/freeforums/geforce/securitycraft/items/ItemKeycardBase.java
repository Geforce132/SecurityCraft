package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.blocks.BlockKeycardReader;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;
import org.freeforums.geforce.securitycraft.timers.ScheduleKeycardUpdate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ItemKeycardBase extends Item{
		
	@SideOnly(Side.CLIENT)
	private IIcon keycardOneIcon;
	 
	@SideOnly(Side.CLIENT)
	private IIcon keycardTwoIcon;
	 
	@SideOnly(Side.CLIENT)
	private IIcon keycardThreeIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon limitedUseKeycardIcon;

	public ItemKeycardBase() {
		this.setHasSubtypes(true);
        this.setMaxDamage(0);
		this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
	
	/**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item par1Item, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(this, 1, 0));
        par3List.add(new ItemStack(this, 1, 1));
        par3List.add(new ItemStack(this, 1, 2));
        par3List.add(new ItemStack(this, 1, 3));
    }
	
	/**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        if(par1 == 0){
        	return this.keycardOneIcon;
        }else if(par1 == 1){
        	return this.keycardTwoIcon;
        }else if(par1 == 2){
        	return this.keycardThreeIcon;
        }else if(par1 == 3){
        	return this.limitedUseKeycardIcon;
        }else{
        	return super.getIconFromDamage(par1);
        }
    }
    
    public String getUnlocalizedName(ItemStack par1ItemStack){
    	if(par1ItemStack.getItemDamage() == 0){
    		return "item.keycardOne";
    	}else if(par1ItemStack.getItemDamage() == 1){
    		return "item.keycardTwo";
    	}else if(par1ItemStack.getItemDamage() == 2){
    		return "item.keycardThree";
    	}else if(par1ItemStack.getItemDamage() == 3){
    		return "item.limitedUseKeycard";
    	}else{
    		return "item.nullItem";
    	}

    }


    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.keycardOneIcon = par1IconRegister.registerIcon("securitycraft:lv1Keycard");
        this.keycardTwoIcon = par1IconRegister.registerIcon("securitycraft:lv2Keycard");
        this.keycardThreeIcon = par1IconRegister.registerIcon("securitycraft:lv3Keycard");
        this.limitedUseKeycardIcon = par1IconRegister.registerIcon("securitycraft:limitedUseKeycard");
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {		
		if(par1ItemStack.getItemDamage() == 3){		
			if(par1ItemStack.stackTagCompound == null){
				par1ItemStack.stackTagCompound = new NBTTagCompound();
				par1ItemStack.stackTagCompound.setInteger("Uses", 5);
			}
			
			par3List.add("Uses remaining: " + par1ItemStack.stackTagCompound.getInteger("Uses"));			
			
		}
	}
	
	/**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par3World.getBlock(par4, par5, par6) == mod_SecurityCraft.keycardReader)
        {
            if (par3World.isRemote)
            {
            	if(((TileEntityKeycardReader)par3World.getTileEntity(par4, par5, par6)).getPassLV() > 0){
            		((TileEntityKeycardReader)par3World.getTileEntity(par4, par5, par6)).setIsProvidingPower(true);
            	}
                return true;
            }
            else
            {      	
            	if(par3World.getBlockMetadata(par4, par5, par6) == 2 || par3World.getBlockMetadata(par4, par5, par6) == 3 || par3World.getBlockMetadata(par4, par5, par6) == 4 || par3World.getBlockMetadata(par4, par5, par6) == 5){              
            		((BlockKeycardReader)mod_SecurityCraft.keycardReader).insertCard(par3World, par4, par5, par6, par1ItemStack, par2EntityPlayer);                
            	}
            	
                return true;
            }
        }
        else
        {
            return false;
        }
    }
    
    public int getKeycardLV(ItemStack par1ItemStack){
		return (par1ItemStack.getItemDamage() + 1);
	}
	

}
