package net.geforcemods.securitycraft.itemgroups;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCTechnicalGroup extends CreativeModeTab
{
	public SCTechnicalGroup()
	{
		super(SecurityCraft.MODID + ".technical");
		setRecipeFolderName(SecurityCraft.MODID);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon()
	{
		return new ItemStack(SCContent.USERNAME_LOGGER.get().asItem());
	}
}
