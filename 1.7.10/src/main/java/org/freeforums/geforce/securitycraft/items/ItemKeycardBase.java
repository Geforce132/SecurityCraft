package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ItemKeycardBase extends Item{
		
	@SideOnly(Side.CLIENT)
	private IIcon keycardOneIcon;
	 
	@SideOnly(Side.CLIENT)
	private IIcon keycardTwoIcon;
	 
	@SideOnly(Side.CLIENT)
	private IIcon keycardThreeIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon keycardFourIcon;
	 
	@SideOnly(Side.CLIENT)
	private IIcon keycardFiveIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon limitedUseKeycardIcon;

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
			if(par1ItemStack.stackTagCompound == null){
				par1ItemStack.stackTagCompound = new NBTTagCompound();
				par1ItemStack.stackTagCompound.setInteger("Uses", 5);
			}
			
			par3List.add("Uses remaining: " + par1ItemStack.stackTagCompound.getInteger("Uses"));			
			
		}
	}

    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1){
        if(par1 == 0){
        	return this.keycardOneIcon;
        }else if(par1 == 1){
        	return this.keycardTwoIcon;
        }else if(par1 == 2){
        	return this.keycardThreeIcon;
        }else if(par1 == 4){
        	return this.keycardFourIcon;
        }else if(par1 == 5){
        	return this.keycardFiveIcon;
        }else if(par1 == 3){
        	return this.limitedUseKeycardIcon;
        }else{
        	return super.getIconFromDamage(par1);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister){
        this.keycardOneIcon = par1IconRegister.registerIcon("securitycraft:lv1Keycard");
        this.keycardTwoIcon = par1IconRegister.registerIcon("securitycraft:lv2Keycard");
        this.keycardThreeIcon = par1IconRegister.registerIcon("securitycraft:lv3Keycard");
        this.keycardFourIcon = par1IconRegister.registerIcon("securitycraft:lv4Keycard");
        this.keycardFiveIcon = par1IconRegister.registerIcon("securitycraft:lv5Keycard");
        this.limitedUseKeycardIcon = par1IconRegister.registerIcon("securitycraft:limitedUseKeycard");
    } 
	
}
