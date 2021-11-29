package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DismountCamera implements IMessage
{
	public DismountCamera() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<DismountCamera, IMessage> {
		@Override
		public IMessage onMessage(DismountCamera message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				EntityPlayerMP player = ctx.getServerHandler().player;

				if(player.getSpectatingEntity() instanceof EntitySecurityCamera)
					((EntitySecurityCamera)player.getSpectatingEntity()).stopViewing(player);
			});

			return null;
		}
	}
}
