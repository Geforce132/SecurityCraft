package net.geforcemods.securitycraft.itemgroups;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCExplosivesGroup extends ItemGroup
{
	public SCExplosivesGroup()
	{
		super(SecurityCraft.MODID + ".explosives");
		setRecipeFolderName(SecurityCraft.MODID);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon()
	{
		return new ItemStack(SCContent.MINE.get().asItem());
	}
}
