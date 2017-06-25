package net.geforcemods.securitycraft.main;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabSCDecoration extends CreativeTabs{
		
	public CreativeTabSCDecoration(){
		super(getNextID(), "tabSecurityCraft");
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsOak);
	}
	
	@Override
	public String getTranslatedTabLabel(){
		return "SecurityCraft: " + ClientUtils.localize("creativeTabDecoration");	
	}

}
