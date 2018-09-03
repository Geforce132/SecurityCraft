package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCPlaySoundAtPos implements IMessage{

	private int x, y, z;
	private String sound;
	private double volume;

	public PacketCPlaySoundAtPos(){

	}

	public PacketCPlaySoundAtPos(int x, int y, int z, String sound, double volume){
		this.x = x;
		this.y = y;
		this.z = z;
		this.sound = sound;
		this.volume = volume;
	}

	public PacketCPlaySoundAtPos(double x, double y, double z, String sound, double volume){
		this((int)x, (int)y, (int)z, sound, volume);
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
