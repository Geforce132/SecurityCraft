package org.freeforums.geforce.securitycraft.main;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
