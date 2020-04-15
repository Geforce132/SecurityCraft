package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
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

	public void fromBytes(ByteBuf buf) {
		rotationYaw = buf.readFloat();
		rotationPitch = buf.readFloat();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeFloat(rotationYaw);
		buf.writeFloat(rotationPitch);
	}

	public static void encode(SetCameraRotation message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SetCameraRotation decode(PacketBuffer packet)
	{
		SetCameraRotation message = new SetCameraRotation();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SetCameraRotation message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();

			if(player.getRidingEntity() instanceof SecurityCameraEntity){
				player.getRidingEntity().rotationYaw = message.rotationYaw;
				player.getRidingEntity().rotationPitch = message.rotationPitch;
			}
		});

		ctx.get().setPacketHandled(true);
	}

}
