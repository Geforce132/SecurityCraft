package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetCameraRotation {

	private float rotationYaw, rotationPitch;

	public SetCameraRotation(){

	}

	public SetCameraRotation(float yaw, float pitch){
		rotationYaw = yaw;
		rotationPitch = pitch;
	}

	public static void encode(SetCameraRotation message, PacketBuffer buf)
	{
		buf.writeFloat(message.rotationYaw);
		buf.writeFloat(message.rotationPitch);
	}

	public static SetCameraRotation decode(PacketBuffer buf)
	{
		SetCameraRotation message = new SetCameraRotation();

		message.rotationYaw = buf.readFloat();
		message.rotationPitch = buf.readFloat();
		return message;
	}

	public static void onMessage(SetCameraRotation message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();

			if(player.getVehicle() instanceof SecurityCameraEntity){
				player.getVehicle().yRot = message.rotationYaw;
				player.getVehicle().xRot = message.rotationPitch;
			}
		});

		ctx.get().setPacketHandled(true);
	}

}
