package net.geforcemods.securitycraft.itemgroups;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SCExplosivesTab extends CreativeTabs {
	public SCExplosivesTab() {
		super(getNextID(), "tabSecurityCraft");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack createIcon() {
		return new ItemStack(Item.getItemFromBlock(SCContent.blockChangeDetectorFloorCeiling));
	}

	@Override
	public String getTabLabel() {
		return super.getTabLabel() + ".explosives";
	}
}
