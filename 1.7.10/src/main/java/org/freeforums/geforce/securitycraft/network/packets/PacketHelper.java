package org.freeforums.geforce.securitycraft.network.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class PacketHelper {
	
	public World getWorld(EntityPlayer par1EntityPlayer){
		return MinecraftServer.getServer().worldServerForDimension(par1EntityPlayer.dimension);
	}
	
	public World getWorld(EntityPlayer par1EntityPlayer, Side side){
		return MinecraftServer.getServer().worldServerForDimension(par1EntityPlayer.dimension);
	}
	
	@SideOnly(Side.CLIENT)
	public World getClientWorld(EntityPlayer par1EntityPlayer){
		return Minecraft.getMinecraft().theWorld;
	}

}
