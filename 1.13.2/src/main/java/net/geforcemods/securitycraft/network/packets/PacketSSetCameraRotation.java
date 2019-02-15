package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketSSetCameraRotation implements IMessage {

	private float rotationYaw, rotationPitch;

	public PacketSSetCameraRotation(){

	}

	public PacketSSetCameraRotation(float yaw, float pitch){
		rotationYaw = yaw;
		rotationPitch = pitch;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		rotationYaw = buf.readFloat();
		rotationPitch = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(rotationYaw);
		buf.writeFloat(rotationPitch);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSSetCameraRotation, IMessage>{

		@Override
		public IMessage onMessage(PacketSSetCameraRotation message, MessageContext ctx) {
			WorldUtils.addScheduledTask(getWorld(ctx.getServerHandler().player), () -> {
				EntityPlayer player = ctx.getServerHandler().player;

				if(player.getRidingEntity() != null && player.getRidingEntity() instanceof EntitySecurityCamera){
					player.getRidingEntity().rotationYaw = message.rotationYaw;
					player.getRidingEntity().rotationPitch = message.rotationPitch;
				}
			});

			return null;
		}

	}

}
