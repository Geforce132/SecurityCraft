package org.freeforums.geforce.securitycraft.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class ItemTestItem extends ItemUsable{

	public ItemTestItem() {
		super();
	}

	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {	
		if(par3World.isRemote){			
			return true;			
		}else{
			par2EntityPlayer.openGui(mod_SecurityCraft.instance, 14, par3World, par4, par5, par6);
			return true;
		}
	}	
	
}
