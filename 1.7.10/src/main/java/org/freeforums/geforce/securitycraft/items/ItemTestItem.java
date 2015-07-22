package org.freeforums.geforce.securitycraft.items;

import org.freeforums.geforce.securitycraft.entity.EntityIMSBomb;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTestItem extends ItemUsable{

	public ItemTestItem() {
		super();
	}

	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {	
		if(!par3World.isRemote){
			par3World.spawnEntityInWorld(new EntityIMSBomb(par3World, (double) par4 + 0.5D, (double) par5 + 1D, (double) par6 + 0.5D));
		}
		
		return true;
	}	
	
}
