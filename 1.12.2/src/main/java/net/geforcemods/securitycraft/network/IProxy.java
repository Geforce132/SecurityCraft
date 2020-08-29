package net.geforcemods.securitycraft.network;

import net.minecraft.entity.player.EntityPlayer;

public interface IProxy
{
	public void registerEntityRenderingHandlers();

	public void registerRenderThings();

	public void registerVariants();

	public EntityPlayer getClientPlayer();
}
