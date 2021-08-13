package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.SecurityCamera;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SetCameraRotation {

	private float rotationYaw, rotationPitch;

	public SetCameraRotation(){

	}

	public SetCameraRotation(float yaw, float pitch){
		rotationYaw = yaw;
		rotationPitch = pitch;
	}

	public static void encode(SetCameraRotation message, FriendlyByteBuf buf)
	{
		buf.writeFloat(message.rotationYaw);
		buf.writeFloat(message.rotationPitch);
	}

	public static SetCameraRotation decode(FriendlyByteBuf buf)
	{
		SetCameraRotation message = new SetCameraRotation();

		message.rotationYaw = buf.readFloat();
		message.rotationPitch = buf.readFloat();
		return message;
	}

	public static void onMessage(SetCameraRotation message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();

			if(player.getVehicle() instanceof SecurityCamera){
				player.getVehicle().setYRot(message.rotationYaw);
				player.getVehicle().setXRot(message.rotationPitch);
			}
		});

		ctx.get().setPacketHandled(true);
	}

}
