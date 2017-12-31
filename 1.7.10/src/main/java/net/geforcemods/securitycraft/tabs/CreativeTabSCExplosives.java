package net.geforcemods.securitycraft.tabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;

public class CreativeTabSCExplosives extends CreativeTabs{

	public CreativeTabSCExplosives(){
		super(getNextID(), "tabSecurityCraft");
	}
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return Item.getItemFromBlock(SCContent.mine);
	}

	@Override
	public String getTranslatedTabLabel(){
		return "SecurityCraft: " + StatCollector.translateToLocal("creativeTabExplosives");

	}
}
