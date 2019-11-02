package org.freeforums.geforce.securitycraft.main;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabSCExplosives extends CreativeTabs{
	
	CreativeTabSCExplosives(int par1, String par2Str){
		super(par1,par2Str);
	}

	
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Mine);
	}
	
	public String getTranslatedTabLabel(){
		return "SecurityCraft: Explosives";
		
	}

}
