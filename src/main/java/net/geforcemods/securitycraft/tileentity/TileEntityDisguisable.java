package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.packets.PacketCRefreshDiguisedModel;
import net.minecraft.item.ItemStack;

public class TileEntityDisguisable extends CustomizableSCTE
{
	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module)
	{
		super.onModuleInserted(stack, module);

		if(!world.isRemote && module == EnumModuleType.DISGUISE)
			SecurityCraft.network.sendToAll(new PacketCRefreshDiguisedModel(pos, true, stack));
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(!world.isRemote && module == EnumModuleType.DISGUISE)
			SecurityCraft.network.sendToAll(new PacketCRefreshDiguisedModel(pos, false, stack));
	}

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[]{EnumModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
