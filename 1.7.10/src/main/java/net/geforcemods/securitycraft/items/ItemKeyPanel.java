package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKeyPanel extends Item {
	
	public ItemKeyPanel(){
		super();
	}
	
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(par3World.getBlock(par4, par5, par6) == mod_SecurityCraft.frame){
				String owner = ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerName();
				String uuid = ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerUUID();
				par3World.setBlock(par4, par5, par6, mod_SecurityCraft.Keypad, par3World.getBlockMetadata(par4, par5, par6), 3);
				((IOwnable) par3World.getTileEntity(par4, par5, par6)).setOwner(uuid, owner);
				par1ItemStack.stackSize -= 1;
			}
			
			return true;
		}
		
		return false;
    }


}
