package net.breakinbad.securitycraft.main;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.breakinbad.securitycraft.main.Utils.BlockUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabSCTechnical extends CreativeTabs{
	
	public static Comparator<ItemStack> itemSorter;
		
	public CreativeTabSCTechnical(){
		super(getNextID(), "tabSecurityCraft");
	}
	
	public void displayAllReleventItems(List items){
		super.displayAllReleventItems(items);
		
		if(itemSorter != null){
			Collections.sort(items, itemSorter);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return BlockUtils.getItemFromBlock(mod_SecurityCraft.usernameLogger);
	}
	
	public String getTranslatedTabLabel(){
		return "SecurityCraft: Technical";
		
	}
}
