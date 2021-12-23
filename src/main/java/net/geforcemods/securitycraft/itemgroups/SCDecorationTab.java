package net.geforcemods.securitycraft.itemgroups;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SCDecorationTab extends CreativeModeTab {
	public SCDecorationTab() {
		super(SecurityCraft.MODID + ".decoration");
		setRecipeFolderName(SecurityCraft.MODID);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(SCContent.REINFORCED_OAK_STAIRS.get().asItem());
	}
}
