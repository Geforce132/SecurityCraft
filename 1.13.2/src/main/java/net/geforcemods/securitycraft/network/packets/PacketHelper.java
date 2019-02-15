package net.geforcemods.securitycraft.network.packets;

import javafx.geometry.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PacketHelper {

	public World getWorld(EntityPlayer player){
		return player.getEntityWorld().getMinecraftServer().getWorld(player.dimension);
	}

	public World getWorld(EntityPlayer player, Side side){
		return player.getEntityWorld().getMinecraftServer().getWorld(player.dimension);
	}

	@OnlyIn(Dist.CLIENT)
	public World getClientWorld(EntityPlayer player){
		return Minecraft.getInstance().world;
	}

}
