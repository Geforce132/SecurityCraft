package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ItemKeycardBase extends Item{

	public ItemKeycardBase() {
		this.setHasSubtypes(true);
        this.setMaxDamage(0);
		this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
	
	public int getKeycardLV(ItemStack par1ItemStack){
		if(par1ItemStack.getItemDamage() == 0){
			return 1;
		}else if(par1ItemStack.getItemDamage() == 1){
			return 2;
		}else if(par1ItemStack.getItemDamage() == 2){
			return 3;
		}else if(par1ItemStack.getItemDamage() == 3){
			return 6;
		}else if(par1ItemStack.getItemDamage() == 4){
			return 4;
		}else if(par1ItemStack.getItemDamage() == 5){
			return 5;
		}else{
			return 0;
		}
	}
	
	/**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item par1Item, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(this, 1, 0)); //1
        par3List.add(new ItemStack(this, 1, 1)); //2
        par3List.add(new ItemStack(this, 1, 2)); //3
        par3List.add(new ItemStack(this, 1, 3)); //LU
        par3List.add(new ItemStack(this, 1, 4)); //4
        par3List.add(new ItemStack(this, 1, 5)); //5
    }
	
	
    
    public String getUnlocalizedName(ItemStack par1ItemStack){
    	if(par1ItemStack.getItemDamage() == 0){
    		return "item.keycardOne";
    	}else if(par1ItemStack.getItemDamage() == 1){
    		return "item.keycardTwo";
    	}else if(par1ItemStack.getItemDamage() == 2){
    		return "item.keycardThree";
    	}else if(par1ItemStack.getItemDamage() == 4){
    		return "item.keycardFour";
    	}else if(par1ItemStack.getItemDamage() == 5){
    		return "item.keycardFive";
    	}else if(par1ItemStack.getItemDamage() == 3){
    		return "item.limitedUseKeycard";
    	}else{
    		return "item.nullItem";
    	}

    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {		
		if(par1ItemStack.getItemDamage() == 3){		
			if(par1ItemStack.getTagCompound() == null){
				par1ItemStack.setTagCompound(new NBTTagCompound());
				par1ItemStack.getTagCompound().setInteger("Uses", 5);
			}
			
			par3List.add("Uses remaining: " + par1ItemStack.getTagCompound().getInteger("Uses"));			
			
		}
	}
	
}
