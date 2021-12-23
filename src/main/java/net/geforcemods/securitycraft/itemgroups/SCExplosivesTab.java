package net.geforcemods.securitycraft.itemgroups;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SCExplosivesTab extends CreativeModeTab {
	public SCExplosivesTab() {
		super(SecurityCraft.MODID + ".explosives");
		setRecipeFolderName(SecurityCraft.MODID);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(SCContent.MINE.get().asItem());
	}
}
