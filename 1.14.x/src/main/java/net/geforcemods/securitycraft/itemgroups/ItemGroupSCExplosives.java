package net.geforcemods.securitycraft.itemgroups;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemGroupSCExplosives extends ItemGroup{

	public ItemGroupSCExplosives(){
		super(GROUPS.length, "tabSecurityCraft");
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon(){
		return new ItemStack(SCContent.mine.asItem());
	}

	@Override
	public String getTabLabel(){
		return super.getTabLabel() + ".explosives";

	}

}
