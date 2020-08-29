package net.geforcemods.securitycraft.network;

import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy implements IProxy
{
	@Override
	public void registerEntityRenderingHandlers() {}

	@Override
	public void registerRenderThings() {}

	@Override
	public void registerVariants() {}

	@Override
	public EntityPlayer getClientPlayer()
	{
		return null;
	}
}
