package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemBriefcase extends Item {
	
	public ItemBriefcase() {}
	
	public boolean isFull3D() {
		return true;
	}
	
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
    	if(par3World.isRemote) {
	    	if(!par1ItemStack.hasTagCompound()) {
	    		par1ItemStack.stackTagCompound = new NBTTagCompound();
	    		ClientUtils.syncItemNBT(par1ItemStack);
	    	}
	    	
	    	if(!par1ItemStack.getTagCompound().hasKey("passcode")) {
	    		par2EntityPlayer.openGui(mod_SecurityCraft.instance, 21, par3World, (int) par2EntityPlayer.posX, (int) par2EntityPlayer.posY, (int) par2EntityPlayer.posZ);
	    	}
	    	else {
	    		par2EntityPlayer.openGui(mod_SecurityCraft.instance, 22, par3World, par4, par5, par6);
	    	}
    	}
    	
    	return false;
    }
    
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {  	
    	if(par2World.isRemote) {
	    	if(!par1ItemStack.hasTagCompound()) {
	    		par1ItemStack.stackTagCompound = new NBTTagCompound();
	    	    ClientUtils.syncItemNBT(par1ItemStack);
	    	}
	    	
	    	if(!par1ItemStack.getTagCompound().hasKey("passcode")) {
	    		par3EntityPlayer.openGui(mod_SecurityCraft.instance, 21, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
	    	}
	    	else {
	    		par3EntityPlayer.openGui(mod_SecurityCraft.instance, 22, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
	    	}
    	}
    	
    	return par1ItemStack;
    }
    
}
