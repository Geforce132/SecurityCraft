package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.effect.EntityLightningBolt;

public class PacketCSpawnLightning implements IMessage{

	private double x, y, z;

	public PacketCSpawnLightning(){

	}

	public PacketCSpawnLightning(double xPos, double yPos, double zPos){
		x = xPos;
		y = yPos;
		z = zPos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCSpawnLightning, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCSpawnLightning packet, MessageContext ctx) {
			EntityLightningBolt lightning = new EntityLightningBolt(Minecraft.getMinecraft().theWorld, packet.x, packet.y, packet.z);
			Minecraft.getMinecraft().theWorld.addWeatherEffect(lightning);

			return null;
		}

	}

}
