package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetCameraRotation implements IMessage {

	private float rotationYaw, rotationPitch;

	public SetCameraRotation(){

	}

	public SetCameraRotation(float yaw, float pitch){
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

	public static class Handler implements IMessageHandler<SetCameraRotation, IMessage>{

		@Override
		public IMessage onMessage(SetCameraRotation message, MessageContext ctx) {
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;

				if(player.getRidingEntity() instanceof EntitySecurityCamera){
					player.getRidingEntity().rotationYaw = message.rotationYaw;
					player.getRidingEntity().rotationPitch = message.rotationPitch;
				}
			});

			return null;
		}

	}

}
