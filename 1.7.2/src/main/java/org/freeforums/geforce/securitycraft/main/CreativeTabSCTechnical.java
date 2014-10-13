package org.freeforums.geforce.securitycraft.main;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabSCTechnical extends CreativeTabs{
	
	CreativeTabSCTechnical(int par1, String par2Str){
		super(par1,par2Str);
	}

	
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return mod_SecurityCraft.remoteAccessMine;
	}
	
	public String getTranslatedTabLabel(){
		return "SecurityCraft: Technical";
		
	}
}
