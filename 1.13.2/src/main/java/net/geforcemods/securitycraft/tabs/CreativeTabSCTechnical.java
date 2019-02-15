package net.geforcemods.securitycraft.tabs;

import javafx.geometry.Side;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabSCTechnical extends CreativeTabs{

	public CreativeTabSCTechnical(){
		super(getNextID(), "tabSecurityCraft");
	}


	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack createIcon(){
		return new ItemStack(Item.getItemFromBlock(SCContent.usernameLogger));
	}

	@Override
	public String getTabLabel(){
		return super.getTabLabel() + ".technical";
	}
}
