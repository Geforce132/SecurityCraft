package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.packets.PacketCRefreshDiguisedModel;
import net.minecraft.item.ItemStack;

public class TileEntityDisguisable extends CustomizableSCTE
{
	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module)
	{
		if(!world.isRemote && module == EnumCustomModules.DISGUISE)
			SecurityCraft.network.sendToAll(new PacketCRefreshDiguisedModel(pos, true, stack));
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module)
	{
		if(!world.isRemote && module == EnumCustomModules.DISGUISE)
			SecurityCraft.network.sendToAll(new PacketCRefreshDiguisedModel(pos, false, stack));
	}

	@Override
	public EnumCustomModules[] acceptedModules()
	{
		return new EnumCustomModules[]{EnumCustomModules.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
