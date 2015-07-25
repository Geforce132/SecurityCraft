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
			double d5 = par2EntityPlayer.posX - ((double) par4 + 0.5D);
            double d6 = par2EntityPlayer.boundingBox.minY + (double)(par2EntityPlayer.height / 2.0F) - ((double) par5 + 13.25D);
            double d7 = par2EntityPlayer.posZ - ((double) par6 + 0.5D);
			
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(par3World, par2EntityPlayer, d5, d6, d7);
            entitylargefireball.posX = (double) par4 + 0.5D;
            entitylargefireball.posY = (double) par5 + 1D;
            entitylargefireball.posZ = (double) par6 + 0.5D;
            par3World.spawnEntityInWorld(entitylargefireball);
		}
		
		return true;
	}	
	
}
