package net.geforcemods.securitycraft.tabs;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class CreativeTabSCTechnical extends CreativeTabs{

	public static Comparator<ItemStack> itemSorter;

	public CreativeTabSCTechnical(){
		super(getNextID(), "tabSecurityCraft");
	}

	@Override
	public void displayAllReleventItems(List items){
		super.displayAllReleventItems(items);

		if(itemSorter != null)
			Collections.sort(items, itemSorter);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return Item.getItemFromBlock(SCContent.usernameLogger);
	}

	@Override
	public String getTranslatedTabLabel(){
		return "SecurityCraft: " + StatCollector.translateToLocal("creativeTabTechnical");

	}
}
