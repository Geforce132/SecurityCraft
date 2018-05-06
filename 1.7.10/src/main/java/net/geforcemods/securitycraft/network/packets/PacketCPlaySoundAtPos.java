package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketCPlaySoundAtPos implements IMessage{

	private int x, y, z;
	private String sound;
	private double volume;

	public PacketCPlaySoundAtPos(){

	}

	public PacketCPlaySoundAtPos(int xPos, int yPos, int zPos, String theSound, double vol){
		x = xPos;
		y = yPos;
		z = zPos;
		sound = theSound;
		volume = vol;
	}

	public PacketCPlaySoundAtPos(double xPos, double yPos, double zPos, String theSound, double vol){
		x = (int) xPos;
		y = (int) yPos;
		z = (int) zPos;
		sound = theSound;
		volume = vol;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		sound = ByteBufUtils.readUTF8String(buf);
		volume = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, sound);
		buf.writeDouble(volume);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCPlaySoundAtPos, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCPlaySoundAtPos message, MessageContext ctx) {
			Minecraft.getMinecraft().theWorld.playSound(message.x,message.y,message.z, message.sound, (float) message.volume, 1.0F, true);
			return null;
		}

	}

}
