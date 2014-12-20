package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemModule extends Item{
	
	private final EnumCustomModules module;
	private final boolean nbtCanBeModified;
	
	public ItemModule(EnumCustomModules module, boolean nbtCanBeModified){
		this.module = module;
		this.nbtCanBeModified = nbtCanBeModified;
		
		this.setMaxStackSize(1);
		this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add(nbtCanBeModified ? "Module is modifiable!" : "Module not modifiable!");
		
		if(module == EnumCustomModules.WHITELIST || module == EnumCustomModules.BLACKLIST){
			par3List.add(" ");
			par3List.add("Players:");

			if(par1ItemStack.stackTagCompound != null){
				for(int i = 1; i <= 10; i++){
					if(!par1ItemStack.stackTagCompound.getString("Player" + i).isEmpty()){
						par3List.add(par1ItemStack.stackTagCompound.getString("Player" + i));
					}
				}
			}
		}
	}

	public EnumCustomModules getModule() {
		return module;
	}
	
	public boolean canBeModified(){
		return this.nbtCanBeModified;
	}

}
