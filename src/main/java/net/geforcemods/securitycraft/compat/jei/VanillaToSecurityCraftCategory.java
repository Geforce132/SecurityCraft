package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.IGuiHelper;
import net.geforcemods.securitycraft.util.Utils;

public class VanillaToSecurityCraftCategory extends BaseCategory
{
	public VanillaToSecurityCraftCategory(IGuiHelper helper)
	{
		super(helper, 0);
	}

	@Override
	public String getTitle()
	{
		return Utils.localize("jei.securitycraft.category.reinforcing").getFormattedText();
	}

	@Override
	public String getUid()
	{
		return SCJEIPlugin.VTS_ID;
	}
}
