package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.util.text.ITextComponent;

public class TileEntityReinforcedPressurePlate extends CustomizableSCTE
{
	public TileEntityReinforcedPressurePlate()
	{
		super(SCContent.teTypeReinforcedPressurePlate);
	}

	@Override
	public EnumCustomModules[] acceptedModules()
	{
		return new EnumCustomModules[] {EnumCustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getCustomName()
	{
		return null;
	}
}
