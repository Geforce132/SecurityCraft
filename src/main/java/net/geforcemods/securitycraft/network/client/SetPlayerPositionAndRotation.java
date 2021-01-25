package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

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

	public static void encode(SetPlayerPositionAndRotation message, PacketBuffer buf)
	{
		buf.writeDouble(message.x);
		buf.writeDouble(message.y);
		buf.writeDouble(message.z);
		buf.writeFloat(message.rotationYaw);
		buf.writeFloat(message.rotationPitch);
	}

	public static SetPlayerPositionAndRotation decode(PacketBuffer buf)
	{
		SetPlayerPositionAndRotation message = new SetPlayerPositionAndRotation();

		message.x = buf.readDouble();
		message.y = buf.readDouble();
		message.z = buf.readDouble();
		message.rotationYaw = buf.readFloat();
		message.rotationPitch = buf.readFloat();
		return message;
	}

	public static void onMessage(SetPlayerPositionAndRotation message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> Minecraft.getInstance().player.setPositionAndRotation(message.x, message.y, message.z, message.rotationYaw, message.rotationPitch));
		ctx.get().setPacketHandled(true);
	}
}
