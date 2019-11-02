package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetPlayerPositionAndRotation{

	private double x, y, z;
	private float rotationYaw, rotationPitch;

	public SetPlayerPositionAndRotation(){

	}

	public SetPlayerPositionAndRotation(double x, double y, double z, float yaw, float pitch){
		this.x = x;
		this.y = y;
		this.z = z;
		rotationYaw = yaw;
		rotationPitch = pitch;
	}

	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		rotationYaw = buf.readFloat();
		rotationPitch = buf.readFloat();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeFloat(rotationYaw);
		buf.writeFloat(rotationPitch);
	}

	public static void encode(SetPlayerPositionAndRotation message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SetPlayerPositionAndRotation decode(PacketBuffer packet)
	{
		SetPlayerPositionAndRotation message = new SetPlayerPositionAndRotation();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SetPlayerPositionAndRotation message, Supplier<NetworkEvent.Context> ctx)
	{
		Minecraft.getInstance().player.setPositionAndRotation(message.x, message.y, message.z, message.rotationYaw, message.rotationPitch);
		ctx.get().setPacketHandled(true);
	}

}
