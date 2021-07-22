package net.geforcemods.securitycraft.itemgroups;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCDecorationGroup extends ItemGroup
{
	public SCDecorationGroup()
	{
		super(SecurityCraft.MODID + ".decoration");
		setRecipeFolderName(SecurityCraft.MODID);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon()
	{
		return new ItemStack(SCContent.REINFORCED_OAK_STAIRS.get().asItem());
	}
}
