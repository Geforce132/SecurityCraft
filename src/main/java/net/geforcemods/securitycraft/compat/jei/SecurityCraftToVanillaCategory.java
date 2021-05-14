package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.IGuiHelper;
import net.geforcemods.securitycraft.util.Utils;

public class SecurityCraftToVanillaCategory extends BaseCategory
{
	public SecurityCraftToVanillaCategory(IGuiHelper helper)
	{
		super(helper, 25);
	}

	@Override
	public String getTitle()
	{
		return Utils.localize("jei.securitycraft.category.unreinforcing").getFormattedText();
	}

	@Override
	public String getUid()
	{
		return SCJEIPlugin.STV_ID;
	}
}
