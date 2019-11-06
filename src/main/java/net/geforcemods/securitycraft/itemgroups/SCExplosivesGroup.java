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
		super(SecurityCraft.MODID);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon()
	{
		return new ItemStack(SCContent.mine.asItem());
	}

	@Override
	public String getTabLabel()
	{
		return super.getTabLabel() + ".explosives";
	}
}
