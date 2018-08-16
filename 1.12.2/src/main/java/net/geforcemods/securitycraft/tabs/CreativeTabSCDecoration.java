package net.geforcemods.securitycraft.tabs;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabSCDecoration extends CreativeTabs{

	public CreativeTabSCDecoration(){
		super(getNextID(), "tabSecurityCraft");
	}


	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack createIcon(){
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedStairsOak));
	}

	@Override
	public String getTabLabel(){
		return "SecurityCraft: " + ClientUtils.localize("creativeTabDecoration");
	}

}
