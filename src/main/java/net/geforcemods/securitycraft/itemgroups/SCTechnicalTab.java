package net.geforcemods.securitycraft.itemgroups;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SCTechnicalTab extends CreativeModeTab {
	public SCTechnicalTab() {
		super(SecurityCraft.MODID + ".technical");
		setRecipeFolderName(SecurityCraft.MODID);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(SCContent.USERNAME_LOGGER.get().asItem());
	}
}
